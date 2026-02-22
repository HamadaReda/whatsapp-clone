package com.wa.whatsappclone.user;

import com.wa.whatsappclone.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserResponse> getAllUsersExceptSelf(Authentication authentication) {
        return userRepository.findAllUsersExceptSelf(authentication.getName())
                .stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    public User getCurrentUser(String keycloakId) {
        return userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new UserNotFoundException(keycloakId));
    }
}
