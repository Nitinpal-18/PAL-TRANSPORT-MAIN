package com.pal_transport.application.service;

import com.pal_transport.application.entity.GoogleUser;
import com.pal_transport.application.entity.User;
import com.pal_transport.application.repos.GoogleUserRepository;
import com.pal_transport.application.repos.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GoogleUserService {
    
    private final GoogleUserRepository googleUserRepository;
    private final UserRepository userRepository;
    

    
    public Optional<GoogleUser> findByEmail(String email) {
        return googleUserRepository.findByEmail(email);
    }
    
    public Optional<GoogleUser> findByProviderId(String providerId) {
        return googleUserRepository.findByProviderId(providerId);
    }
    
    public boolean existsByEmail(String email) {
        return googleUserRepository.existsByEmail(email);
    }
    
    public boolean existsByProviderId(String providerId) {
        return googleUserRepository.existsByProviderId(providerId);
    }
    
    public GoogleUser createGoogleUser(String email, String name, String providerId, String pictureUrl) {
        log.info("Creating new Google user: {}", email);
        
        // Split name into first and last name
        String[] nameParts = name.split(" ", 2);
        String firstName = nameParts[0];
        String lastName = nameParts.length > 1 ? nameParts[1] : "";
        
        GoogleUser googleUser = GoogleUser.builder()
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .providerId(providerId)
                .pictureUrl(pictureUrl)
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();
        
        GoogleUser savedUser = googleUserRepository.save(googleUser);
        log.info("Successfully created Google user with ID: {}", savedUser.getId());
        return savedUser;
    }
    
    public GoogleUser updateLastLogin(GoogleUser googleUser) {
        googleUser.setLastLoginAt(LocalDateTime.now());
        return googleUserRepository.save(googleUser);
    }
    
    /**
     * Check if a Google user should be upgraded to a registered user
     * This happens when an admin registers a user with the same email
     */
    public boolean shouldUpgradeToRegisteredUser(String email) {
        // Check if there's a registered user with this email
        return userRepository.existsByEmail(email);
    }
    
    /**
     * Upgrade a Google user to a registered user
     * This transfers the user from google_users table to users table
     */
    public User upgradeToRegisteredUser(String email, String role) {
        log.info("Upgrading Google user to registered user: {}", email);
        
        GoogleUser googleUser = googleUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Google user not found: " + email));
        
        User registeredUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Registered user not found: " + email));
        
        // Update the registered user with Google user information
        registeredUser.setFirstName(googleUser.getFirstName());
        registeredUser.setLastName(googleUser.getLastName());
        registeredUser.setProviderId(googleUser.getProviderId());
        registeredUser.setPictureUrl(googleUser.getPictureUrl());
        registeredUser.setProvider(User.AuthProvider.GOOGLE);
        
        // Save the updated registered user
        User savedUser = userRepository.save(registeredUser);
        
        // Delete the Google user record
        googleUserRepository.delete(googleUser);
        
        log.info("Successfully upgraded Google user to registered user with role: {}", role);
        return savedUser;
    }
} 