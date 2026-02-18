package com.wa.whatsappclone.exception;

public class SelfChatNotAllowedException extends RuntimeException {
    public SelfChatNotAllowedException() {
        super("Cannot create chat with yourself");
    }
}
