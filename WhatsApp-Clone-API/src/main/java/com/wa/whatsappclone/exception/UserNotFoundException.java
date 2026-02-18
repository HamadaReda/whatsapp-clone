package com.wa.whatsappclone.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String keycloakId) {
        super("User not found with keycloakId: " + keycloakId);
    }
}
