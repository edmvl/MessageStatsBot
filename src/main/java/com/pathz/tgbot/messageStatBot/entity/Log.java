package com.pathz.tgbot.messageStatBot.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "log")
public class Log {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "chat_id")
    String chatId;

    @Column(name = "date_time")
    LocalDateTime dateTime;

    @Column(name = "user_id")
    String userId;

    @Column(name = "user_name")
    String userName;

    @Column(name = "user_last_name")
    String userLastName;

    @Column(name = "user_first_name")
    String userFirstName;

    @Column(name = "chat_name")
    String chatName;

    @Column(name = "text")
    String text;

    @Column(name = "filetype")
    String fileType;

    @Column(name = "file")
    String file;

    @Column(name = "logger_type")
    String loggerType;

}
