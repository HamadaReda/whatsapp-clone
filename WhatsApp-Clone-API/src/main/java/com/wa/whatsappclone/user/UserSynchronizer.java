package com.wa.whatsappclone.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSynchronizer {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public void synchronizeWithIdP(Jwt token) {

        String keycloakId = token.getSubject();
        if (keycloakId == null) {
            log.warn("Token does not contain subject (sub)");
            return;
        }

        userRepository.findById(keycloakId).ifPresentOrElse(
                existing -> updateExistingUser(existing, token),
                () -> createNewUser(token)
        );

    }


    private void createNewUser(Jwt token) {
        log.info("Creating new user with keycloak id {}", token.getSubject());
        User newUser = userMapper.fromToken(token);
        userRepository.save(newUser);
    }

    private void updateExistingUser(User existing, Jwt token) {
        log.info("Updating existing user with keycloak id {}", token.getSubject());
        userMapper.updateFromToken(existing, token);
        userRepository.save(existing);
    }
}
