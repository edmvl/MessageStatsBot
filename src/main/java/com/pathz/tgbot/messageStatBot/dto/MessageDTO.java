package com.pathz.tgbot.messageStatBot.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {
    private String from;
    private Long chatId;
    private String chatName;
    private Integer messageId;
    private Integer replyMessageId;
    private Long userId;
    private String userName;
    private String userText;

}
