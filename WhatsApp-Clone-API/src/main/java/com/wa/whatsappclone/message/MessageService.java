package com.wa.whatsappclone.message;

import com.wa.whatsappclone.chat.Chat;
import com.wa.whatsappclone.chat.ChatRepository;
import com.wa.whatsappclone.exception.ChatNotFoundException;
import com.wa.whatsappclone.exception.UserNotFoundException;
import com.wa.whatsappclone.file.FileService;
import com.wa.whatsappclone.user.User;
import com.wa.whatsappclone.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final MessageMapper messageMapper;
    private final UserRepository userRepository;
    private final FileService fileService;

    public void saveMessage(MessageRequest messageRequest, Authentication authentication) {
        User sender = getCurrentUser(authentication);

        Chat chat = getChat(messageRequest.getChatId());

        User receiver = chat.getOtherUser(sender.getId());

        Message message = messageMapper.toTextMessage(
                messageRequest,
                chat,
                sender.getId(),
                receiver.getId()
        );

        messageRepository.save(message);

        // todo notification
    }

    public Slice<MessageResponse> getChatMessagesSlice(
            String chatId,
            int page,
            int size
    ){
        Pageable pageable = PageRequest.of(page, size);
        return messageRepository.findByChatIdOrderByCreatedDateDesc(chatId, pageable)
                .map(messageMapper::toMessageResponse);
    }

    public void uploadMediaMessage(String chatId, MultipartFile file, Authentication authentication) {
        Chat chat = getChat(chatId);
        User currentUser = getCurrentUser(authentication);
        String currentUserId = currentUser.getId();
        User otherUser   = chat.getOtherUser(currentUserId);
        String otherUserId = otherUser.getId();
        String filePath = fileService.saveFile(file, currentUserId);

        Message message = messageMapper.toFileMessage(
                filePath,
                chat,
                currentUserId,
                otherUserId
        );
        messageRepository.save(message);
        // todo notification
    }

    @Transactional
    public void markMessagesAsRead(String chatId, Authentication authentication) {
        Chat chat = getChat(chatId);
        User currentUser = getCurrentUser(authentication);
        messageRepository.markMessagesAsRead(chatId, currentUser.getId());
        // todo notification
    }

    private Chat getChat(String chatId) {
        return chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException(chatId));
    }

    private User getCurrentUser(Authentication authentication) {
        String keycloakId = authentication.getName();
        return userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new UserNotFoundException(keycloakId));
    }

}
