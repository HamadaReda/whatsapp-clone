package com.wa.whatsappclone.message;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageResponse {

    private Long id;
    private String content;
    private MessageType type;
    private MessageState state;
    private String senderId;
    private String receiverId;
    private LocalDateTime createdAt;
    private byte[] media;
    /*
        لو هترجع media file كبير في كل request
        ده هيضرب الأداء.
        الأفضل:
        تخزن media في storage
        تبعت URL
    تعمل endpoint منفصل لتحميله
    */

}
