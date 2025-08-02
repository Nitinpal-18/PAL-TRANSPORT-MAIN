package com.pal_transport.application.service;

import com.pal_transport.application.entity.User;
import com.pal_transport.application.entity.GoogleUser;
import com.pal_transport.config.SecurityProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {
    
    private final SecurityProperties securityProperties;
    
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }
    
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, securityProperties.getJwt().getExpirationMs());
    }
    
    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, securityProperties.getJwt().getRefreshExpirationMs());
    }
    
    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuer(securityProperties.getJwt().getIssuer())
                .setAudience(securityProperties.getJwt().getAudience())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), Jwts.SIG.HS256)
                .compact();
    }
    
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
        } catch (Exception e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }
    
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("JWT token expired: {}", e.getMessage());
            throw new JwtException("Token expired");
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT token: {}", e.getMessage());
            throw new JwtException("Unsupported token");
        } catch (MalformedJwtException e) {
            log.warn("Malformed JWT token: {}", e.getMessage());
            throw new JwtException("Malformed token");
        } catch (SecurityException e) {
            log.warn("JWT signature validation failed: {}", e.getMessage());
            throw new JwtException("Invalid signature");
        } catch (IllegalArgumentException e) {
            log.warn("JWT token is empty or null: {}", e.getMessage());
            throw new JwtException("Empty token");
        } catch (JwtException e) {
            log.error("Error parsing JWT token: {}", e.getMessage());
            throw new JwtException("Invalid JWT token");
        }
    }
    
    private SecretKey getSignInKey() {
        byte[] keyBytes = securityProperties.getJwt().getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    public String generateTokenForUser(User user) {
        Map<String, Object> claims = createUserClaims(user);
        return generateToken(claims, user);
    }
    
    public String generateTokenForUser(GoogleUser googleUser) {
        Map<String, Object> claims = createGoogleUserClaims(googleUser);
        return generateToken(claims, googleUser);
    }
    
    private Map<String, Object> createUserClaims(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("role", user.getRole().name());
        claims.put("provider", user.getProvider().name());
        claims.put("name", user.getName());
        return claims;
    }
    
    private Map<String, Object> createGoogleUserClaims(GoogleUser googleUser) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", googleUser.getId());
        claims.put("role", "USER"); // Google users are always regular users
        claims.put("provider", "GOOGLE");
        claims.put("name", googleUser.getName());
        return claims;
    }
    
    public Long extractUserId(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.get("userId", Long.class);
        } catch (Exception e) {
            log.warn("Failed to extract user ID from token: {}", e.getMessage());
            return null;
        }
    }
    
    public User.Role extractUserRole(String token) {
        try {
            Claims claims = extractAllClaims(token);
            String roleStr = claims.get("role", String.class);
            return User.Role.valueOf(roleStr);
        } catch (Exception e) {
            log.warn("Failed to extract user role from token: {}", e.getMessage());
            return User.Role.USER; // Default to USER role
        }
    }
    
    public User.AuthProvider extractUserProvider(String token) {
        try {
            Claims claims = extractAllClaims(token);
            String providerStr = claims.get("provider", String.class);
            return User.AuthProvider.valueOf(providerStr);
        } catch (Exception e) {
            log.warn("Failed to extract user provider from token: {}", e.getMessage());
            return User.AuthProvider.EMAIL; // Default to EMAIL provider
        }
    }
} 