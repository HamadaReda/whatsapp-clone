package com.wa.whatsappclone.chat;

import com.wa.whatsappclone.common.StringResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chats")
@Tag(name = "Chat")
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
