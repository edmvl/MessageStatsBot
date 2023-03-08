package com.pathz.tgbot.messageStatBot.dto;

import lombok.*;

import java.time.LocalDate;

public interface StatsViewDto {
    public String getChatId();
    public String getUserId();
    public int getCount();
}
