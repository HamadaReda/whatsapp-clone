package com.wa.whatsappclone.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSynchronizer {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public void synchronizeWithIdP(Jwt token) {
        String keycloakId = token.getSubject();
        String email = token.getClaimAsString("email");

        Optional<User> optionalUser = userRepository.findByKeycloakId(keycloakId)
                                        .or(() -> userRepository.findByEmail(email));

        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            if (existingUser.getKeycloakId() == null) {
                existingUser.setKeycloakId(keycloakId);
            }
            updateExistingUser(existingUser, token);
        } else {
            createNewUser(token);
        }
    }


    private void createNewUser(Jwt token) {
        log.info("Creating new user with keycloak id {}", token.getSubject());
        try {
            userRepository.save(userMapper.fromToken(token));
        } catch (DataIntegrityViolationException e) {
            log.debug("User already created");
        }
    }

    private void updateExistingUser(User existing, Jwt token) {
        log.info("Updating existing user with keycloak id {}", token.getSubject());
        userMapper.updateFromToken(existing, token);
    }
}
