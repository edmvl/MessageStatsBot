package com.pathz.tgbot.messageStatBot.util.enums;

import lombok.Getter;

public enum ChatSettingConstants {
    ENABLE_STINKY("enable_stinky"),
    ENABLE_HOLIDAYS("enable_holidays"),
    ENABLE_STATS("enable_stats");


    @Getter
    private final String dbValue;

    ChatSettingConstants(String dbValue) {
        this.dbValue = dbValue;
    }

}
