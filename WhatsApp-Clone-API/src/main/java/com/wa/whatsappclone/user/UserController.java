package com.wa.whatsappclone.user;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "User")
public class UserController {

    private final UserService userService;
    private final UserSynchronizer userSynchronizer;

    @GetMapping
    public ResponseEntity<List<UserResponse>> findAllUsers(Authentication authentication) {
        return ResponseEntity.ok(
                userService.getAllUsersExceptSelf(authentication)
        );
    }

    @GetMapping("/user/{keycloak-id}")
    public ResponseEntity<UserResponse> findUserByKeycloakId(@PathVariable("keycloak-id") String keycloakId, Authentication authentication) {
        Jwt token = (Jwt)authentication.getPrincipal();
        userSynchronizer.synchronizeWithIdP(token);
        return ResponseEntity.ok(
                userService.findUserByKeycloakId(keycloakId)
        );
    }
}
