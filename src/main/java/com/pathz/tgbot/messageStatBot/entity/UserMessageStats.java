package com.pathz.tgbot.messageStatBot.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "user_message_stats")
public class UserMessageStats {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "username")
    String username;

    @Column(name = "count")
    Integer count;

    @Override
    public String toString() {
        return "UserMessageStats{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", count=" + count +
                '}';
    }
}
