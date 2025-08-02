package com.pal_transport.application.dto;

import com.pal_transport.application.entity.User;
import com.pal_transport.application.entity.GoogleUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDTO {
    
    private String token;
    private String refreshToken;
    private UserDTO user;
    
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserDTO {
        private Long id;
        private String email;
        private String name;
        private String role;
        private String provider;
        private String pictureUrl;
    }
    
    public static AuthResponseDTO fromUser(User user, String token, String refreshToken) {
        return AuthResponseDTO.builder()
                .token(token)
                .refreshToken(refreshToken)
                .user(UserDTO.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .name(user.getName())
                        .role(user.getRole().name())
                        .provider(user.getProvider().name())
                        .pictureUrl(user.getPictureUrl())
                        .build())
                .build();
    }
    
    public static AuthResponseDTO fromGoogleUser(GoogleUser googleUser, String token, String refreshToken) {
        return AuthResponseDTO.builder()
                .token(token)
                .refreshToken(refreshToken)
                .user(UserDTO.builder()
                        .id(googleUser.getId())
                        .email(googleUser.getEmail())
                        .name(googleUser.getName())
                        .role("USER") // Google users are always regular users
                        .provider("GOOGLE")
                        .pictureUrl(googleUser.getPictureUrl())
                        .build())
                .build();
    }
} 