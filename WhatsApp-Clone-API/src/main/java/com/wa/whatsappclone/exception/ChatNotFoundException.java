package com.wa.whatsappclone.exception;

public class ChatNotFoundException extends RuntimeException {
    public ChatNotFoundException(String chatId) {
        super("Chat not found with chatId: " + chatId);
    }
}
