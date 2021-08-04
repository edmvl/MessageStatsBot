package com.pathz.tgbot.messageStatBot.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Data
@Table(name = "stats")
public class Stats {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "message")
    String message;

    @Column(name = "count")
    Integer count;

    @Override
    public String toString() {
        return "Stats{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", count=" + count +
                '}';
    }
}
