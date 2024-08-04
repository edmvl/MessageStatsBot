package com.pathz.tgbot.messageStatBot.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "taxi")
public class Taxi {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "user_id")
    String userId;

    @Column(name = "phone_number")
    String phoneNumber;

    @Column(name = "comment")
    String comment;

    @Column(name = "online")
    boolean online;

    @Column(name = "busy")
    boolean busy;

}
