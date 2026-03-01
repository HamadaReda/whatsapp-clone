package com.wa.whatsappclone.user;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "User")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> findAllUsers(Authentication authentication) {
        return ResponseEntity.ok(
                userService.getAllUsersExceptSelf(authentication)
        );
    }

    @GetMapping("/user/{keycloak-id}")
    public ResponseEntity<UserResponse> findUserByKeycloakId(@PathVariable("keycloak-id") String keycloakId) {
        return ResponseEntity.ok(
                userService.findUserByKeycloakId(keycloakId)
        );
    }
}
