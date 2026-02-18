package com.wa.whatsappclone.chat;

import com.wa.whatsappclone.exception.SelfChatNotAllowedException;
import com.wa.whatsappclone.exception.UserNotFoundException;
import com.wa.whatsappclone.message.MessageRepository;
import com.wa.whatsappclone.message.MessageType;
import com.wa.whatsappclone.user.User;
import com.wa.whatsappclone.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final ChatMapper chatMapper;
    private final MessageRepository messageRepository;

    private static record LastMessageInfo(
            String content,
            LocalDateTime date
    ) {}

    public List<ChatResponse> getChatsForCurrentUser(Authentication authentication) {
        String keycloakId = authentication.getName();
        String userId = userRepository
                .findByKeycloakId(keycloakId)
                .orElseThrow(() -> new UserNotFoundException(keycloakId))
                .getId();

        List<Chat> chats = chatRepository.findAllByUserId(userId);

        return chats.stream()
                .map(chat -> {
                    Optional<LastMessageInfo> lastMessageInfo = getLastMessage(chat.getId());
                    return chatMapper.toChatResponse(
                            chat,
                            userId,
                            getUnreadMessagesCount(chat.getId(), userId),
                            lastMessageInfo.map(LastMessageInfo::content).orElse(null),
                            lastMessageInfo.map(LastMessageInfo::date).orElse(null)
                    );
                })
                .collect(Collectors.toList());
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

    public Optional<LastMessageInfo> getLastMessage(String chatId) {
        return messageRepository
                .findFirstByChatIdOrderByCreatedDateDesc(chatId)
                .map(message -> {
                    if (message.getType() != MessageType.TEXT)
                        return new LastMessageInfo("Attachment", message.getCreatedDate());
                    else
                        return new LastMessageInfo(message.getContent(), message.getCreatedDate());
                });

    }

    public long getUnreadMessagesCount(String chatId, String currentUserId) {
        return messageRepository
                .countUnreadMessages(chatId, currentUserId);
    }

//    public Optional<ChatResponse> getChatBetweenUsers(String currentUserId, String otherUserId) {
//        return chatRepository.findBetweenUsers(currentUserId, otherUserId)
//                .map(chat -> mapper.toChatResponse(chat, currentUserId));
//    }

}
