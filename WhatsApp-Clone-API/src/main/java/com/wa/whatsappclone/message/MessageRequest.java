package com.wa.whatsappclone.message;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageRequest {

    private String content;
    private MessageType type;
    private String chatId;

}
