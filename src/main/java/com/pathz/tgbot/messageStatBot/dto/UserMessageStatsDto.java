package com.pathz.tgbot.messageStatBot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserMessageStatsDto {

    private String username;
    private Integer count;

    @Override
    public String toString() {
        return username + ": " + count;
    }
}
