package com.wa.whatsappclone.common;

public record ChatResponseMessage(
        String chatId
) {
    public static ChatResponseMessage of(String chatId) {
        return new ChatResponseMessage(chatId);
    }
}
