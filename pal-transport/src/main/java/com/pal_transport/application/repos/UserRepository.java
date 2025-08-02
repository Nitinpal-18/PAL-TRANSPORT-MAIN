package com.pal_transport.application.repos;

import com.pal_transport.application.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByProviderId(String providerId);
    
    boolean existsByEmail(String email);
    
    boolean existsByProviderId(String providerId);
    
    Optional<User> findByEmailAndProvider(String email, User.AuthProvider provider);
    
    /**
     * Count users by role
     * @param role User role to count
     * @return Count of users with the specified role
     */
    long countByRole(User.Role role);
} 