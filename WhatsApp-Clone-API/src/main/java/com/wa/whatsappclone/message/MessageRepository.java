package com.wa.whatsappclone.message;

import com.wa.whatsappclone.chat.Chat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByChatIdOrderByCreatedDateDesc(String chatId);

    Slice<Message> findByChatIdOrderByCreatedDateDesc(String chatId, Pageable pageable);

    Optional<Message> findFirstByChatIdOrderByCreatedDateDesc(String chatId);

    @Query("""
        SELECT COUNT(*)
        FROM Message m
        WHERE m.chat.id =:chatId
        AND m.receiverId = :userId
        AND m.state = com.wa.whatsappclone.message.MessageState.SENT
    """)
    long countUnreadMessages(String chatId, String userId);

    @Modifying
    @Query("""
        UPDATE Message m
        SET m.state = com.wa.whatsappclone.message.MessageState.SEEN,
            m.seenAt = CURRENT_TIMESTAMP
        WHERE m.chat.id =:chatId
            AND m.senderId = :otherUserId
            AND m.state = com.wa.whatsappclone.message.MessageState.SENT
    """)
    int markMessagesAsRead(String chatId, String otherUserId);
}
