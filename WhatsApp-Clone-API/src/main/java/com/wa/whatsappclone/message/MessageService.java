package com.wa.whatsappclone.message;

import com.wa.whatsappclone.chat.Chat;
import com.wa.whatsappclone.chat.ChatService;
import com.wa.whatsappclone.file.FileService;
import com.wa.whatsappclone.file.FileUtils;
import com.wa.whatsappclone.notification.Notification;
import com.wa.whatsappclone.notification.NotificationService;
import com.wa.whatsappclone.notification.NotificationType;
import com.wa.whatsappclone.user.User;
import com.wa.whatsappclone.user.UserService;
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
    private final ChatService chatService;
    private final MessageMapper messageMapper;
    private final UserService userService;
    private final FileService fileService;
    private final NotificationService notificationService;

    public void saveMessage(MessageRequest messageRequest, Authentication authentication) {
        User sender = userService.getCurrentUser(authentication.getName());

        Chat chat = chatService.getChat(messageRequest.getChatId());

        User receiver = chat.getOtherUser(sender.getId());
        String chatName = receiver.getFullName();

        Message message = messageMapper.toTextMessage(
                messageRequest,
                chat,
                sender.getId(),
                receiver.getId()
        );

        messageRepository.save(message);

        Notification notification = Notification.builder()
                .chatId(chat.getId())
                .content(messageRequest.getContent())
                .senderId(sender.getId())
                .receiverId(receiver.getId())
                .chatName(chatName)
                .messageType(messageRequest.getType())
                .type(NotificationType.MESSAGE)
                .build();
        notificationService.sendNotification(receiver.getId(), notification);
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
        Chat chat = chatService.getChat(chatId);
        User currentUser = userService.getCurrentUser(authentication.getName());
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
        Notification notification = Notification.builder()
                .chatId(chat.getId())
                .senderId(currentUserId)
                .receiverId(otherUserId)
                .messageType(MessageType.IMAGE)
                .type(NotificationType.IMAGE)
                .media(FileUtils.readFileFromLocation(filePath))
                .build();
        notificationService.sendNotification(otherUserId, notification);
    }

    @Transactional
    public void markMessagesAsRead(String chatId, Authentication authentication) {
        Chat chat = chatService.getChat(chatId);
        User currentUser = userService.getCurrentUser(authentication.getName());
        User otherUser   = chat.getOtherUser(currentUser.getId());
        messageRepository.markMessagesAsRead(chatId, currentUser.getId());
        Notification notification = Notification.builder()
                .chatId(chat.getId())
                .senderId(currentUser.getId())
                .receiverId(otherUser.getId())
                .type(NotificationType.SEEN)
                .build();
        notificationService.sendNotification(otherUser.getId(), notification);
    }





}
