package com.wa.whatsappclone.chat;

import com.wa.whatsappclone.user.User;
import org.springframework.stereotype.Component;

@Component
public class ChatMapper {

    public ChatResponse toChatResponse(Chat chat, String currentUserId) {
        User otherUser = chat.getOtherUser(currentUserId);

        return ChatResponse.builder()
                .id(chat.getId())
                .name(otherUser.getFullName())
                .unreadCount(chat.getUnreadMessageCount(currentUserId))
                .lastMessage(chat.getLastMessage())
                .lastMessageTime(chat.getLastMessageTime())
                .isRecipientOnline(otherUser.isUserOnline())
                .senderId(currentUserId)                // senderId(chat.getSender().getId())
                .recipientId(otherUser.getId())         // recipientId(chat.getRecipient().getId())
                .build();
    }
}
