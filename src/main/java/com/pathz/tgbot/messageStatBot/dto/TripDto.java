package com.pathz.tgbot.messageStatBot.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TripDto {
    private Long id;
    private String userId;
    private LocalDateTime dateTime;
    private String startFrom;
    private String destination;
    private int seat;
    private boolean published;
}
