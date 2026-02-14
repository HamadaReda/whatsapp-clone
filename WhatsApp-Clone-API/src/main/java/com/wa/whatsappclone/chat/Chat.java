package com.wa.whatsappclone.chat;

import com.wa.whatsappclone.common.BaseAuditingEntity;
import com.wa.whatsappclone.message.Message;
import com.wa.whatsappclone.message.MessageState;
import com.wa.whatsappclone.message.MessageType;
import com.wa.whatsappclone.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

import static jakarta.persistence.GenerationType.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "chat")
@NamedQuery(name = ChatConstants.FIND_CHAT_BY_CURRENT_USER_ID,
            query = "SELECT c FROM Chat c WHERE c.sender.id = :currentUserId OR c.recipient.id = :currentUserId ORDER BY c.createdDate DESC")
@NamedQuery(name = ChatConstants.FIND_CHAT_BY_SENDER_ID_AND_RECEIVER_ID,
            query = "SELECT c FROM Chat c WHERE (c.sender.id = :senderId AND c.recipient.id = :receiverId) OR (c.sender.id = :receiverId AND c.recipient.id = :senderId)")
public class Chat extends BaseAuditingEntity {

    @Id
    @GeneratedValue(strategy = UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private User recipient;

    @OneToMany(mappedBy = "chat", fetch = FetchType.LAZY)
    @OrderBy("createdDate DESC")
    private List<Message> messages;

//    @Transient
//    public String getChatName(final String currentUserId) {
//        if (sender.getId().equals(currentUserId)) {
//            return sender.getFirstName() + " " + sender.getLastName();
//        }
//        return recipient.getFirstName() + " " + recipient.getLastName();
//    }

    @Transient
    public User getOtherUser(final String currentUserId) {
        if (sender.getId().equals(currentUserId)) {
            return recipient;
        }
        return sender;
    }

    @Transient
    public Long getUnreadMessageCount(final String currentUserId) {
        return messages
                .stream()
                .filter(m -> m.getReceiverId().equals(currentUserId))
                .filter(m -> m.getState() == MessageState.SENT)
                .count();
    }

    @Transient
    public String getLastMessage(){
        if (messages != null && !messages.isEmpty()) {
            if(messages.getFirst().getType() != MessageType.TEXT){
                return "Attachment";
            }
            return messages.getFirst().getContent();
        }
        return null;
    }

    @Transient
    public LocalDateTime getLastMessageTime(){
        if (messages != null && !messages.isEmpty()) {
            return messages.getFirst().getCreatedDate();
        }
        return null;
    }
}
