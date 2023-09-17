package com.pathz.tgbot.messageStatBot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatsDto {
    String chatId;
    String userId;
    LocalDate date;
    int count;
    String login;
    String name;
    @Override
    public String toString() {
        return chatId + "," + userId + ": " + count;
    }
}
