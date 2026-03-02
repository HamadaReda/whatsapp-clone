package com.wa.whatsappclone.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendNotification(String userKeycloakId, Notification notification) {
        log.info("Sending WS notification to {} with payload {}", userKeycloakId, notification);
        messagingTemplate.convertAndSendToUser(
                userKeycloakId,
                "/chat",
                notification
        );
    }

}
