package com.pathz.tgbot.messageStatBot.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "quiz_chat")
public class QuizChat {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "chat_id")
    String chatId;

    @Column(name = "question_id")
    Long questionId;

    @Column(name = "winner_id")
    String winnerId;

    @Column(name = "message_id")
    Integer messageId;

    @Column(name = "attempt")
    Integer attempt;

}
