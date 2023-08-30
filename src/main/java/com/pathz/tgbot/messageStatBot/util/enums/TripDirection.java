package com.pathz.tgbot.messageStatBot.util.enums;

public enum TripDirection {
    CHEBOKSARY("Чебоксары","Урмары - Чебоксары"),
    URMARY("Чебоксары","Чебоксары - Урмары");

    private final String name;
    private final String description;

    TripDirection(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }
}
