package com.pathz.tgbot.messageStatBot.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "remind")
public class Remind {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "user_id")
    String userId;

    @Column(name = "chat_id")
    String chatId;

    @Column(name = "reply_message_id")
    String replyMessageId;

    @Column(name = "date_time")
    LocalDateTime dateTime;

    @Column(name = "text")
    String text;

    @Column(name = "active")
    boolean active;

}
