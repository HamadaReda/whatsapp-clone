package com.wa.whatsappclone.user;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public User getOrCreateUser(Jwt token) {

        String keycloakId = token.getSubject();

        return userRepository
                .findByKeycloakId(keycloakId)
                .orElseGet(() -> createUser(token));
    }

    private User createUser(Jwt token) {

        try {
            User user = userMapper.fromToken(token);
            return userRepository.saveAndFlush(user);
        }
        catch (DataIntegrityViolationException ex) {
            // user created by another request
            return userRepository
                    .findByKeycloakId(token.getSubject())
                    .orElseThrow();
        }
    }
}
