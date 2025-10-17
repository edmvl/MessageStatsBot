package com.pathz.tgbot.messageStatBot.util.enums;

import lombok.Getter;

public enum ChatSettingConstants {
    ENABLE_SELECTED("enable_selected"),
    ENABLE_HOLIDAYS("enable_holidays"),
    ENABLE_STATS("enable_stats"),
    TRIP_CHANNEL("trip_channel"),
    ;


    @Getter
    private final String dbValue;

    ChatSettingConstants(String dbValue) {
        this.dbValue = dbValue;
    }

}
