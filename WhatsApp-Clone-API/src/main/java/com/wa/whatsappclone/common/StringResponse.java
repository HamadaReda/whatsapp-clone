package com.wa.whatsappclone.common;

public record StringResponse(
        String chatId
) {
    public static StringResponse of(String chatId) {
        return new StringResponse(chatId);
    }
}
