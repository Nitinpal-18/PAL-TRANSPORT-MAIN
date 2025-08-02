package com.pal_transport.application.service;

import com.pal_transport.application.entity.User;
import com.pal_transport.application.repos.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pal_transport.application.dto.UserRegistrationDTO;
import com.pal_transport.application.exceptions.UserRegistrationException;
import com.pal_transport.application.service.GoogleUserService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final GoogleUserService googleUserService;
    

    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public Optional<User> findByProviderId(String providerId) {
        return userRepository.findByProviderId(providerId);
    }
    
    public User createUser(User user) {
        if (user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        user.setProvider(User.AuthProvider.EMAIL);
        return userRepository.save(user);
    }
    
    public User createGoogleUser(String email, String name, String providerId, String pictureUrl) {
        log.info("Processing Google OAuth for email: {}", email);
        
        // Check if user already exists with this email
        Optional<User> existingUser = findByEmail(email);
        
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            log.info("User already exists with email: {}, updating Google OAuth info", email);
            
            // Update existing user with Google OAuth information
            user.setProviderId(providerId);
            user.setPictureUrl(pictureUrl);
            user.setProvider(User.AuthProvider.GOOGLE);
            
            // Update name if it's different (Google name might be more accurate)
            String[] nameParts = name.split(" ", 2);
            String firstName = nameParts[0];
            String lastName = nameParts.length > 1 ? nameParts[1] : "";
            
            // Only update name if it's different from current
            if (!firstName.equals(user.getFirstName()) || !lastName.equals(user.getLastName())) {
                user.setFirstName(firstName);
                user.setLastName(lastName);
                log.info("Updated user name from Google OAuth: {} {}", firstName, lastName);
            }
            
            // Update last login
            user.setLastLoginAt(LocalDateTime.now());
            
            User savedUser = userRepository.save(user);
            log.info("Successfully updated existing user with Google OAuth: {}", savedUser.getId());
            return savedUser;
        }
        
        // Create new user with Google OAuth
        log.info("Creating new user with Google OAuth for email: {}", email);
        String[] nameParts = name.split(" ", 2);
        String firstName = nameParts[0];
        String lastName = nameParts.length > 1 ? nameParts[1] : "";
        
        User user = User.builder()
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .providerId(providerId)
                .pictureUrl(pictureUrl)
                .provider(User.AuthProvider.GOOGLE)
                .role(User.Role.USER) // Default to USER role for new Google OAuth users
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();
        
        User savedUser = userRepository.save(user);
        log.info("Successfully created new user with Google OAuth: {}", savedUser.getId());
        return savedUser;
    }
    
    public User updateLastLogin(User user) {
        user.setLastLoginAt(LocalDateTime.now());
        return userRepository.save(user);
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    public boolean existsByProviderId(String providerId) {
        return userRepository.existsByProviderId(providerId);
    }
    
    public User save(User user) {
        return userRepository.save(user);
    }
    
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
    
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        return userRepository.findById(userId)
                .filter(user -> user.getPassword() != null && 
                        passwordEncoder.matches(oldPassword, user.getPassword()))
                .map(user -> {
                    user.setPassword(passwordEncoder.encode(newPassword));
                    userRepository.save(user);
                    return true;
                })
                .orElse(false);
    }
    
    /**
     * Register a new user with validation
     */
    public User registerUser(UserRegistrationDTO registrationDTO) {
        log.info("Registering new user with email: {}", registrationDTO.getEmail());
        
        // Check if email already exists in registered users
        if (existsByEmail(registrationDTO.getEmail())) {
            throw new UserRegistrationException("User with email " + registrationDTO.getEmail() + " already exists");
        }
        
        // Check if there's a Google user with this email that needs to be upgraded
        boolean hasGoogleUser = googleUserService.existsByEmail(registrationDTO.getEmail());
        
        // Validate admin role limit
        if ("ADMIN".equals(registrationDTO.getRole())) {
            long adminCount = userRepository.countByRole(User.Role.ADMIN);
            if (adminCount >= 2) {
                throw new UserRegistrationException("Maximum number of administrators (2) already reached");
            }
        }
        
        // Create user
        User user = User.builder()
                .firstName(registrationDTO.getFirstName())
                .lastName(registrationDTO.getLastName())
                .email(registrationDTO.getEmail())
                .phoneNumber(registrationDTO.getPhoneNumber())
                .password(registrationDTO.getPassword())
                .profilePhoto(registrationDTO.getProfilePhoto())
                .role(User.Role.valueOf(registrationDTO.getRole()))
                .provider(User.AuthProvider.EMAIL)
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();
        
        User savedUser = createUser(user);
        
        // If there was a Google user, upgrade them
        if (hasGoogleUser) {
            log.info("Upgrading existing Google user to registered user: {}", registrationDTO.getEmail());
            savedUser = googleUserService.upgradeToRegisteredUser(registrationDTO.getEmail(), registrationDTO.getRole());
        }
        
        log.info("Successfully registered user with ID: {}", savedUser.getId());
        return savedUser;
    }
    
    /**
     * Get count of users by role
     */
    public long getAdminCount() {
        return userRepository.countByRole(User.Role.ADMIN);
    }
    
    /**
     * Check if admin registration is allowed
     */
    public boolean canRegisterAdmin() {
        return getAdminCount() < 2;
    }
    
    /**
     * Link existing user to Google OAuth
     * This allows staff users to use Google OAuth for login
     */
    public User linkUserToGoogleOAuth(String email, String providerId, String pictureUrl) {
        log.info("Linking user to Google OAuth: {}", email);
        
        Optional<User> existingUser = findByEmail(email);
        if (existingUser.isEmpty()) {
            throw new UserRegistrationException("User with email " + email + " not found");
        }
        
        User user = existingUser.get();
        
        // Update user with Google OAuth information
        user.setProviderId(providerId);
        user.setPictureUrl(pictureUrl);
        user.setProvider(User.AuthProvider.GOOGLE);
        user.setLastLoginAt(LocalDateTime.now());
        
        User savedUser = userRepository.save(user);
        log.info("Successfully linked user to Google OAuth: {}", savedUser.getId());
        return savedUser;
    }
} 