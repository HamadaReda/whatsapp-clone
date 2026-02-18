package com.wa.whatsappclone.chat;

import com.wa.whatsappclone.common.StringResponse;
import com.wa.whatsappclone.message.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
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
    public ResponseEntity<StringResponse> createChat(
            @RequestParam(name = "sender-id") String senderId,
            @RequestParam(name = "receiver-id") String receiverId
    ) {
        final String chatId = chatService.createChat(senderId, receiverId);
        return ResponseEntity.ok(StringResponse.of(chatId));
    }

    @GetMapping
    public ResponseEntity<List<ChatResponse>> GetAllChats(Authentication authentication) {
        return ResponseEntity.ok(chatService.getChatsForCurrentUser(authentication));
    }


}
