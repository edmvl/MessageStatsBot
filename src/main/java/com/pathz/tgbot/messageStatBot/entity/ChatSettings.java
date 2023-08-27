package com.pathz.tgbot.messageStatBot.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "chat_settings")
public class ChatSettings {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "chat_id")
    String chatId;

    @Column(name = "setting_name")
    String settingName;

    @Column(name = "setting_value")
    String settingValue;

}
