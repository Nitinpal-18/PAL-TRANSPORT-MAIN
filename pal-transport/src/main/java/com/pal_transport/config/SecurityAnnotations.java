package com.pal_transport.config;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom security annotations for method-level authorization
 */
public class SecurityAnnotations {
    
    /**
     * Requires ADMIN role
     */
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @PreAuthorize("hasRole('ADMIN')")
    public @interface RequireAdmin {}
    
    /**
     * Requires either ADMIN role or the user to be the owner of the resource
     */
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @PreAuthorize("hasRole('ADMIN') or @securityService.isOwner(#userId)")
    public @interface RequireAdminOrOwner {}
    
    /**
     * Requires authentication (any authenticated user)
     */
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @PreAuthorize("isAuthenticated()")
    public @interface RequireAuth {}
    
    /**
     * Requires either ADMIN role or USER role
     */
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public @interface RequireAnyRole {}
} 