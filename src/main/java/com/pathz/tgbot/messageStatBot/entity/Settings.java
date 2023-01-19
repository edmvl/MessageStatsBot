package com.pathz.tgbot.messageStatBot.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "settings")
public class Settings {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "chat_id")
    String chatId;

    @Column(name = "user_id")
    String userId;

    @Column(name = "skip_stats")
    Boolean skipStats;

    @Column(name = "is_admin")
    Boolean isAdmin;

    @Override
    public String toString() {
        return "Stats{" +
                "id=" + id +
                ", chatId ='" + chatId + '\'' +
                ", userId='" + userId + '\'' +
                ", skipStats=" + skipStats +
                ", isAdmin=" + isAdmin +
                '}';
    }
}
