package com.wa.whatsappclone.chat;

import com.wa.whatsappclone.common.ChatResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestControllerAdvice
@RequiredArgsConstructor
@RequestMapping("/api/v1/chats")
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<ChatResponseMessage> createChat(
            @RequestParam(name = "sender-id") String senderId,
            @RequestParam(name = "receiver-id") String receiverId
    ) {
        final String chatId = chatService.createChat(senderId, receiverId);
        return ResponseEntity.ok(ChatResponseMessage.of(chatId));
    }

    @GetMapping
    public ResponseEntity<List<ChatResponse>> GetAllChats(Authentication authentication) {
        return ResponseEntity.ok(chatService.getChatsForCurrentUser(authentication));
    }

}
