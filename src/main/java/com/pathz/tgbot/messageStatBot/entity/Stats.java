package com.pathz.tgbot.messageStatBot.entity;

import lombok.*;

import javax.persistence.*;
import java.sql.Date;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "stats")
public class Stats {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "chat_id")
    String chatId;

    @Column(name = "user_id")
    String userId;

    @Column(name = "date")
    LocalDate date;

    @Column(name = "login")
    String login;

    @Column(name = "name")
    String name;

    @Column(name = "count")
    Integer count;

    @Override
    public String toString() {
        return "Stats{" +
                "id=" + id +
                ", chatId ='" + chatId + '\'' +
                ", userId='" + userId + '\'' +
                ", date=" + date +
                ", count=" + count +
                '}';
    }
}
