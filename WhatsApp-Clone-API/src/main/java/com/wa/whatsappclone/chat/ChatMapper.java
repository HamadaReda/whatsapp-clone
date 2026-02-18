package com.wa.whatsappclone.chat;

import com.wa.whatsappclone.user.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ChatMapper {

    public ChatResponse toChatResponse(
            Chat chat,
            String currentUserId,
            long unreadCount,
            String lastMessage,
            LocalDateTime lastMessageDate
    ) {
        User otherUser = chat.getOtherUser(currentUserId);


        return ChatResponse.builder()
                .id(chat.getId())
                .name(otherUser.getFullName())
                .unreadCount(unreadCount)
                .lastMessage(lastMessage)
                .lastMessageTime(lastMessageDate)
                .isRecipientOnline(otherUser.isUserOnline())
                .currentUserId(currentUserId)                // currentUserId(chat.getSender().getId())
                .otherUserId(otherUser.getId())         // otherUserId(chat.getRecipient().getId())
                .build();
    }


}
