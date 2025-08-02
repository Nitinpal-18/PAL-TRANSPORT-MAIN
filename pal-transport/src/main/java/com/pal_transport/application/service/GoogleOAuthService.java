package com.pal_transport.application.service;

import com.pal_transport.application.dto.AuthResponseDTO;
import com.pal_transport.application.entity.User;
import com.pal_transport.config.SecurityProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleOAuthService {
    
    private final SecurityProperties securityProperties;
    private final UserService userService;
    private final GoogleUserService googleUserService;
    private final JwtService jwtService;
    private final RestTemplate restTemplate = new RestTemplate();
    
    private static final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final String GOOGLE_USER_INFO_URL = "https://www.googleapis.com/oauth2/v2/userinfo";
    
    public AuthResponseDTO authenticateWithGoogle(String code, String state) {
        try {
            log.info("Starting Google OAuth authentication for code: {} and state: {}", 
                    code.substring(0, Math.min(10, code.length())) + "...", state);
            
            // Exchange authorization code for access token
            String accessToken = exchangeCodeForToken(code);
            
            // Get user information from Google
            GoogleUserInfo googleUserInfo = getUserInfoFromGoogle(accessToken);
            log.info("Retrieved Google user info: email={}, name={}", googleUserInfo.getEmail(), googleUserInfo.getName());
            
            // Find or create user
            Object user = findOrCreateGoogleUser(googleUserInfo);
            
            // Update last login
            if (user instanceof User) {
                user = userService.updateLastLogin((User) user);
            } else if (user instanceof com.pal_transport.application.entity.GoogleUser) {
                user = googleUserService.updateLastLogin((com.pal_transport.application.entity.GoogleUser) user);
            }
            
            // Generate JWT tokens
            return generateAuthResponse(user);
            
        } catch (Exception e) {
            log.error("Google OAuth authentication failed: {}", e.getMessage(), e);
            throw new RuntimeException("Google authentication failed: " + e.getMessage());
        }
    }
    
    private String exchangeCodeForToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", securityProperties.getGoogleOAuth().getClientId());
        body.add("client_secret", securityProperties.getGoogleOAuth().getClientSecret());
        body.add("code", code);
        body.add("grant_type", "authorization_code");
        body.add("redirect_uri", securityProperties.getGoogleOAuth().getRedirectUri());
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(GOOGLE_TOKEN_URL, request, String.class);
            
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Failed to exchange code for token. Status: " + response.getStatusCode());
            }
            
            JSONObject jsonResponse = new JSONObject(response.getBody());
            if (jsonResponse.has("error")) {
                throw new RuntimeException("Google OAuth error: " + jsonResponse.getString("error_description"));
            }
            
            return jsonResponse.getString("access_token");
            
        } catch (Exception e) {
            log.error("Failed to exchange code for token: {}", e.getMessage());
            throw new RuntimeException("Failed to exchange authorization code for access token: " + e.getMessage());
        }
    }
    
    private GoogleUserInfo getUserInfoFromGoogle(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        
        HttpEntity<String> request = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    GOOGLE_USER_INFO_URL, 
                    HttpMethod.GET, 
                    request, 
                    String.class
            );
            
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Failed to get user info from Google. Status: " + response.getStatusCode());
            }
            
            JSONObject userInfo = new JSONObject(response.getBody());
            
            return GoogleUserInfo.builder()
                    .id(userInfo.getString("id"))
                    .email(userInfo.getString("email"))
                    .name(userInfo.getString("name"))
                    .picture(userInfo.optString("picture", ""))
                    .givenName(userInfo.optString("given_name", ""))
                    .familyName(userInfo.optString("family_name", ""))
                    .build();
                    
        } catch (Exception e) {
            log.error("Failed to get user info from Google: {}", e.getMessage());
            throw new RuntimeException("Failed to get user information from Google: " + e.getMessage());
        }
    }
    
    private Object findOrCreateGoogleUser(GoogleUserInfo googleUserInfo) {
        // First, try to find existing user by email
        Optional<User> existingUser = userService.findByEmail(googleUserInfo.getEmail());
        
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            log.info("Found existing user with email: {}", googleUserInfo.getEmail());
            
            // Update user with Google OAuth information if needed
            if (user.getProvider() != User.AuthProvider.GOOGLE) {
                user.setProviderId(googleUserInfo.getId());
                user.setPictureUrl(googleUserInfo.getPicture());
                user.setProvider(User.AuthProvider.GOOGLE);
                user = userService.save(user);
                log.info("Updated existing user with Google OAuth info: {}", user.getId());
            }
            
            return user;
        }
        
        // Check if there's a Google user with this provider ID
        Optional<com.pal_transport.application.entity.GoogleUser> existingGoogleUser = 
                googleUserService.findByProviderId(googleUserInfo.getId());
        
        if (existingGoogleUser.isPresent()) {
            log.info("Found existing Google user with provider ID: {}", googleUserInfo.getId());
            return existingGoogleUser.get();
        }
        
        // Create new Google user
        log.info("Creating new Google user for email: {}", googleUserInfo.getEmail());
        return googleUserService.createGoogleUser(
                googleUserInfo.getEmail(),
                googleUserInfo.getName(),
                googleUserInfo.getId(),
                googleUserInfo.getPicture()
        );
    }
    
    private AuthResponseDTO generateAuthResponse(Object user) {
        if (user instanceof User) {
            User registeredUser = (User) user;
            String token = jwtService.generateTokenForUser(registeredUser);
            String refreshToken = jwtService.generateRefreshToken(registeredUser);
            return AuthResponseDTO.fromUser(registeredUser, token, refreshToken);
        } else if (user instanceof com.pal_transport.application.entity.GoogleUser) {
            com.pal_transport.application.entity.GoogleUser googleUser = (com.pal_transport.application.entity.GoogleUser) user;
            String token = jwtService.generateTokenForUser(googleUser);
            String refreshToken = jwtService.generateRefreshToken(googleUser);
            return AuthResponseDTO.fromGoogleUser(googleUser, token, refreshToken);
        } else {
            throw new RuntimeException("Unknown user type");
        }
    }
    
    @lombok.Data
    @lombok.Builder
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class GoogleUserInfo {
        private String id;
        private String email;
        private String name;
        private String picture;
        private String givenName;
        private String familyName;
    }
} 