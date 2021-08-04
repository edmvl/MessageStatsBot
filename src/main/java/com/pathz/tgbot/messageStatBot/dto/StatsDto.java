package com.pathz.tgbot.messageStatBot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatsDto {

    String message;
    int count;

    @Override
    public String toString() {
        return message + ": " + count;
    }
}
