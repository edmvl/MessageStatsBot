package com.pathz.tgbot.messageStatBot.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "challenge")
public class Challenge {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "chat_id")
    String chatId;

    @Column(name = "chat_name")
    String chatName;

    @Column(name = "date_time_start")
    LocalDateTime dateTimeStart;

    @Column(name = "date_time_end")
    LocalDateTime dateTimeEnd;

    @Column(name = "description")
    String description;

    @Column(name = "finished")
    Boolean finished;

}
