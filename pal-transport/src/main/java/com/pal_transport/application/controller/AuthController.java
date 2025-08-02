package com.pal_transport.application.controller;

import com.pal_transport.application.dto.AuthRequestDTO;
import com.pal_transport.application.dto.AuthResponseDTO;
import com.pal_transport.application.dto.GoogleOAuthRequestDTO;
import com.pal_transport.application.entity.User;
import com.pal_transport.application.exceptions.SecurityException;
import com.pal_transport.application.service.GoogleOAuthService;
import com.pal_transport.application.service.JwtService;
import com.pal_transport.application.service.SecurityAuditService;
import com.pal_transport.application.service.CompositeUserDetailsService;
import com.pal_transport.application.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final CompositeUserDetailsService compositeUserDetailsService;
    private final JwtService jwtService;
    private final GoogleOAuthService googleOAuthService;
    private final SecurityAuditService securityAuditService;
    
    @Operation(summary = "Login with email and password")
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO request, 
                                               HttpServletRequest httpRequest) {
        log.info("Login attempt for user: {}", request.getEmail());
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new SecurityException.AuthenticationFailedException("User not found"));
            
            // Update last login
            user = userService.updateLastLogin(user);
            
            // Generate tokens
            String token = jwtService.generateTokenForUser(user);
            String refreshToken = jwtService.generateRefreshToken(user);
            
            AuthResponseDTO response = AuthResponseDTO.fromUser(user, token, refreshToken);
            
            // Log successful login
            securityAuditService.logSecurityEvent("LOGIN_SUCCESS", 
                String.format("User %s logged in successfully", user.getEmail()),
                getClientIpAddress(httpRequest), httpRequest.getHeader("User-Agent"),
                httpRequest.getMethod(), httpRequest.getRequestURI(), 200, null);
            
            log.info("User logged in successfully: {}", user.getEmail());
            return ResponseEntity.ok(response);
            
        } catch (BadCredentialsException e) {
            // Log failed login attempt
            securityAuditService.logSecurityEvent("LOGIN_FAILED", 
                String.format("Failed login attempt for user: %s - Invalid credentials", request.getEmail()),
                getClientIpAddress(httpRequest), httpRequest.getHeader("User-Agent"),
                httpRequest.getMethod(), httpRequest.getRequestURI(), 401, null);
            
            log.warn("Login failed for user: {} - Invalid credentials", request.getEmail());
            throw new SecurityException.AuthenticationFailedException("Invalid credentials");
            
        } catch (Exception e) {
            // Log authentication error
            securityAuditService.logSecurityEvent("LOGIN_ERROR", 
                String.format("Login error for user: %s - %s", request.getEmail(), e.getMessage()),
                getClientIpAddress(httpRequest), httpRequest.getHeader("User-Agent"),
                httpRequest.getMethod(), httpRequest.getRequestURI(), 500, null);
            
            log.error("Login failed for user: {}", request.getEmail(), e);
            throw new SecurityException.AuthenticationFailedException("Authentication failed");
        }
    }
    
    @Operation(summary = "Authenticate with Google OAuth")
    @PostMapping("/google")
    public ResponseEntity<AuthResponseDTO> authenticateWithGoogle(@Valid @RequestBody GoogleOAuthRequestDTO request,
                                                                HttpServletRequest httpRequest) {
        try {
            log.info("Received Google OAuth request with code: {} and state: {}", 
                    request.getCode().substring(0, Math.min(10, request.getCode().length())) + "...", 
                    request.getState());
            
            AuthResponseDTO response = googleOAuthService.authenticateWithGoogle(request.getCode(), request.getState());
            
            // Log successful OAuth login
            securityAuditService.logSecurityEvent("OAUTH_SUCCESS", 
                String.format("Google OAuth successful for user: %s", response.getUser().getEmail()),
                getClientIpAddress(httpRequest), httpRequest.getHeader("User-Agent"),
                httpRequest.getMethod(), httpRequest.getRequestURI(), 200, null);
            
            log.info("Google OAuth authentication successful for user: {}", response.getUser().getEmail());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            // Log OAuth failure
            securityAuditService.logSecurityEvent("OAUTH_FAILED", 
                String.format("Google OAuth failed: %s", e.getMessage()),
                getClientIpAddress(httpRequest), httpRequest.getHeader("User-Agent"),
                httpRequest.getMethod(), httpRequest.getRequestURI(), 400, null);
            
            log.error("Google OAuth authentication failed", e);
            throw new SecurityException.OAuthAuthenticationException("Google authentication failed: " + e.getMessage());
        }
    }
    
    @Operation(summary = "Refresh JWT token")
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refreshToken(@RequestHeader("Authorization") String authHeader,
                                                      HttpServletRequest httpRequest) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new SecurityException.InvalidTokenException("Invalid authorization header");
            }
            
            String refreshToken = authHeader.substring(7);
            String email = jwtService.extractUsername(refreshToken);
            
            UserDetails userDetails = compositeUserDetailsService.loadUserByUsername(email);
            
            if (jwtService.isTokenValid(refreshToken, userDetails)) {
                User user = userService.findByEmail(email)
                        .orElseThrow(() -> new SecurityException.AuthenticationFailedException("User not found"));
                
                String newToken = jwtService.generateTokenForUser(user);
                String newRefreshToken = jwtService.generateRefreshToken(user);
                
                AuthResponseDTO response = AuthResponseDTO.fromUser(user, newToken, newRefreshToken);
                
                // Log successful token refresh
                securityAuditService.logSecurityEvent("TOKEN_REFRESH", 
                    String.format("Token refreshed successfully for user: %s", email),
                    getClientIpAddress(httpRequest), httpRequest.getHeader("User-Agent"),
                    httpRequest.getMethod(), httpRequest.getRequestURI(), 200, null);
                
                log.info("Token refreshed successfully for user: {}", email);
                return ResponseEntity.ok(response);
            } else {
                throw new SecurityException.InvalidTokenException("Invalid refresh token");
            }
            
        } catch (Exception e) {
            // Log token refresh failure
            securityAuditService.logSecurityEvent("TOKEN_REFRESH_FAILED", 
                String.format("Token refresh failed: %s", e.getMessage()),
                getClientIpAddress(httpRequest), httpRequest.getHeader("User-Agent"),
                httpRequest.getMethod(), httpRequest.getRequestURI(), 401, null);
            
            log.error("Token refresh failed", e);
            throw new SecurityException.InvalidTokenException("Token refresh failed: " + e.getMessage());
        }
    }
    
    @Operation(summary = "Get current user information")
    @GetMapping("/me")
    public ResponseEntity<AuthResponseDTO.UserDTO> getCurrentUser(@RequestHeader("Authorization") String authHeader,
                                                                HttpServletRequest httpRequest) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new SecurityException.InvalidTokenException("Invalid authorization header");
            }
            
            String token = authHeader.substring(7);
            String email = jwtService.extractUsername(token);
            
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new SecurityException.AuthenticationFailedException("User not found"));
            
            AuthResponseDTO.UserDTO userDTO = AuthResponseDTO.UserDTO.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .name(user.getName())
                    .role(user.getRole().name())
                    .provider(user.getProvider().name())
                    .pictureUrl(user.getPictureUrl())
                    .build();
            
            // Log user info request
            securityAuditService.logSecurityEvent("USER_INFO_REQUEST", 
                String.format("User info requested for: %s", email),
                getClientIpAddress(httpRequest), httpRequest.getHeader("User-Agent"),
                httpRequest.getMethod(), httpRequest.getRequestURI(), 200, null);
            
            return ResponseEntity.ok(userDTO);
            
        } catch (Exception e) {
            log.error("Failed to get current user", e);
            throw new SecurityException.AuthenticationFailedException("Failed to get current user: " + e.getMessage());
        }
    }
    
    @Operation(summary = "Test endpoint to verify authentication")
    @GetMapping("/test")
    public ResponseEntity<String> testAuth(HttpServletRequest httpRequest) {
        try {
            String userEmail = "unknown";
            if (SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetails) {
                userEmail = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
            }
            
            log.info("Test endpoint called by user: {}", userEmail);
            return ResponseEntity.ok("Authentication working! User: " + userEmail);
            
        } catch (Exception e) {
            log.error("Error in test endpoint", e);
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
    
    @Operation(summary = "Logout (client-side token removal)")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest httpRequest) {
        // JWT tokens are stateless, so logout is handled client-side
        // But we can log the logout event
        securityAuditService.logSecurityEvent("LOGOUT", 
            "User logged out",
            getClientIpAddress(httpRequest), httpRequest.getHeader("User-Agent"),
            httpRequest.getMethod(), httpRequest.getRequestURI(), 200, null);
        
        log.info("User logged out");
        return ResponseEntity.ok("Logged out successfully");
    }
    
    /**
     * Get client IP address from request
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0];
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
} 