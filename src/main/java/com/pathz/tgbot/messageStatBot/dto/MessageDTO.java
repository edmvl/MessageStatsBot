package com.pathz.tgbot.messageStatBot.dto;

import lombok.*;

import java.time.LocalDateTime;

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
    private LocalDateTime dateTime;
    private Long userId;
    private String userName;
    private String userFirstName;
    private String userLastName;
    private String userPhone;
    private String userText;

}
