package com.wa.whatsappclone.user;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserMapper {

    public User fromToken(Jwt token) {
        return User.builder()
                .keycloakId(token.getSubject())
                .firstName(resolveFirstName(token))
                .lastName(token.getClaimAsString("family_name"))
                .email(token.getClaimAsString("email"))
                .lastSeen(LocalDateTime.now())
                .build();
    }

    public User updateFromToken(User user, Jwt token) {
        String firstName = resolveFirstName(token);
        if (firstName != null) {
            user.setFirstName(firstName);
        }
        String lastName = token.getClaimAsString("family_name");
        if (lastName != null) {
            user.setLastName(lastName);
        }
        String email = token.getClaimAsString("email");
        if (email != null) {
            user.setEmail(email);
        }
        user.setLastSeen(LocalDateTime.now());
        return user;
    }

    private String resolveFirstName(Jwt token) {
        if (token.getClaims().containsKey("given_name")) {
            return token.getClaimAsString("given_name");
        }
        return token.getClaimAsString("nickname");
    }

    public UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .keycloakId(user.getKeycloakId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .lastSeen(user.getLastSeen())
                .isOnline(user.isUserOnline())
                .build();
    }

}
