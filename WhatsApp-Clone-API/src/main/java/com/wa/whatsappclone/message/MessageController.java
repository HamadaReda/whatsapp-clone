package com.wa.whatsappclone.message;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/messages")
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void saveMessage(@RequestBody MessageRequest message, Authentication authentication) {
        messageService.saveMessage(message, authentication);
    }

    @PostMapping(value = "upload-media", consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    public void uploadMedia(
            @RequestParam("chat-id") String chatId,
            // todo add @Parameter from swagger
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) {
        messageService.uploadMediaMessage(chatId, file, authentication);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void markMessageAsRead(@RequestParam("chat-id") String chatId, Authentication authentication) {
        messageService.markMessagesAsRead(chatId, authentication);
    }

    @GetMapping("/chat/{chat-id}")
    public ResponseEntity<Slice<MessageResponse>> getMessages(
            @PathVariable("chat-id") String chatId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ){
        return ResponseEntity.ok(
                messageService.getChatMessagesSlice(chatId, page, size)
        );
    }


}
