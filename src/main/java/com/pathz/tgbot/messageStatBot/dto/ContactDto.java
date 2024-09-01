package com.pathz.tgbot.messageStatBot.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContactDto {
    private String userId;
    private String driverId;
    private String tripId;
}