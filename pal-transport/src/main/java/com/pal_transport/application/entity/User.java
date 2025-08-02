package com.pal_transport.application.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(unique = true, nullable = false)
    private String email;
    
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    @Column(name = "first_name", nullable = false)
    private String firstName;
    
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    @Column(name = "last_name", nullable = false)
    private String lastName;
    
    @Size(max = 15, message = "Phone number cannot exceed 15 characters")
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Column(name = "profile_photo")
    private String profilePhoto;
    
    @Size(max = 255, message = "Password cannot exceed 255 characters")
    @Column
    private String password;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.USER;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AuthProvider provider = AuthProvider.EMAIL;
    
    @Column(name = "provider_id")
    private String providerId;
    
    @Column(name = "picture_url")
    private String pictureUrl;
    
    @Column(name = "is_enabled", nullable = false)
    @Builder.Default
    private boolean enabled = true;
    
    @Column(name = "is_account_non_expired", nullable = false)
    @Builder.Default
    private boolean accountNonExpired = true;
    
    @Column(name = "is_account_non_locked", nullable = false)
    @Builder.Default
    private boolean accountNonLocked = true;
    
    @Column(name = "is_credentials_non_expired", nullable = false)
    @Builder.Default
    private boolean credentialsNonExpired = true;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
    
    @Override
    public String getUsername() {
        return email;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }
    
    @Override
    public boolean isEnabled() {
        return enabled;
    }
    
    // Getter for lastLoginAt to match SecurityController expectations
    public LocalDateTime getLastLogin() {
        return lastLoginAt;
    }
    
    // Getter for full name
    public String getName() {
        return firstName + " " + lastName;
    }
    
    public enum Role {
        ADMIN, STAFF, USER
    }
    
    public enum AuthProvider {
        EMAIL, GOOGLE
    }
} 