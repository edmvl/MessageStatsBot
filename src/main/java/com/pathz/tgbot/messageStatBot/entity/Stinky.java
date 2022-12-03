package com.pathz.tgbot.messageStatBot.entity;

import lombok.Data;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Data
@Table(name = "stinky")
public class Stinky {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "chat_id")
    String chatId;

    @Column(name = "user_id")
    String userId;

    @Column(name = "date")
    Date date;

    @Override
    public String toString() {
        return "Stats{" +
                "id=" + id +
                ", chatId ='" + chatId + '\'' +
                ", userId='" + userId + '\'' +
                ", count=" + date +
                '}';
    }
}
