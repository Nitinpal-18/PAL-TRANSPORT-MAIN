package com.pal_transport.application.repos;

import com.pal_transport.application.entity.GoogleUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GoogleUserRepository extends JpaRepository<GoogleUser, Long> {
    
    Optional<GoogleUser> findByEmail(String email);
    
    Optional<GoogleUser> findByProviderId(String providerId);
    
    boolean existsByEmail(String email);
    
    boolean existsByProviderId(String providerId);
} 