package com.pathz.tgbot.messageStatBot.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "challenge_reg")
public class ChallengeReg {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "challenge_id")
    Long challengeId;

    @Column(name = "user_id")
    String userId;

    @Column(name = "user_name")
    String userName;

}
