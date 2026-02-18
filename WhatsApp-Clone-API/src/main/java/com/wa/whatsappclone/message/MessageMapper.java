package com.wa.whatsappclone.message;

import com.wa.whatsappclone.chat.Chat;
import com.wa.whatsappclone.file.FileUtils;
import org.springframework.stereotype.Service;

@Service
public class MessageMapper {

    public Message toTextMessage(
            MessageRequest messageRequest,
            Chat chat,
            String senderId,
            String receiverId
    ) {
        Message message = new Message();
        message.setChat(chat);
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(messageRequest.getContent());
        message.setType(messageRequest.getType());
        message.setState(MessageState.SENT);
        return message;
    }

    public Message toFileMessage(
            String filePath,
            Chat chat,
            String senderId,
            String receiverId
    ){
        Message message = new Message();
        message.setChat(chat);
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setMediaFilePath(filePath);
        message.setType(MessageType.IMAGE);
        message.setState(MessageState.SENT);
        return message;
    }

    public MessageResponse toMessageResponse(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .content(message.getContent())
                .type(message.getType())
                .state(message.getState())
                .senderId(message.getSenderId())
                .receiverId(message.getReceiverId())
                .createdAt(message.getCreatedDate())
                .media(FileUtils.readFileFromLocation(message.getMediaFilePath()))
                .build();
    }

}
