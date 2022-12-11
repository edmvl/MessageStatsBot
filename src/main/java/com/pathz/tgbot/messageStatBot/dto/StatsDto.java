package com.pathz.tgbot.messageStatBot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.sql.Date;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatsDto {

    String chatId;
    String userId;
    LocalDate date;
    int count;
    String login;

    @Override
    public String toString() {
        return chatId + "," + userId + ": " + count;
    }
}
