package com.wa.whatsappclone.chat;

import com.wa.whatsappclone.exception.SelfChatNotAllowedException;
import com.wa.whatsappclone.exception.UserNotFoundException;
import com.wa.whatsappclone.user.User;
import com.wa.whatsappclone.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final ChatMapper chatMapper;

    public List<ChatResponse> getChatsForCurrentUser(Authentication authentication) {
        String keycloakId = authentication.getName();
        String userId = userRepository
                .findByKeycloakId(keycloakId)
                .orElseThrow(() -> new UserNotFoundException(keycloakId))
                .getId();

        List<Chat> chats = chatRepository.findAllByUserId(userId);

        return (List<ChatResponse>) chats.stream()
                .map(chat -> chatMapper.toChatResponse(chat, userId))
                .toList();
    }

    public String createChat(String senderKeycloakId, String receiverKeycloakId) {
        User sender = userRepository.findByKeycloakId(senderKeycloakId)
                .orElseThrow(() ->
                        new UserNotFoundException(senderKeycloakId));

        User recipient = userRepository.findByKeycloakId(receiverKeycloakId)
                .orElseThrow(() ->
                        new UserNotFoundException(receiverKeycloakId));

        if (sender.getId().equals(recipient.getId())) {
            throw new SelfChatNotAllowedException();
        }

        return chatRepository.findBetweenUsers(sender.getId(), recipient.getId())
                .map(Chat::getId)
                .orElseGet(() -> {
                    Chat chat = new Chat();
                    chat.setSender(sender);
                    chat.setRecipient(recipient);
                    return chatRepository.save(chat).getId();
                });

    }

//    public Optional<ChatResponse> getChatBetweenUsers(String currentUserId, String otherUserId) {
//        return chatRepository.findBetweenUsers(currentUserId, otherUserId)
//                .map(chat -> mapper.toChatResponse(chat, currentUserId));
//    }

}
