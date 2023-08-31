package com.pathz.tgbot.messageStatBot.util.enums;

public enum TripDirection {
    URMARY_CHEBOKSARY("Урмары","Чебоксары"),
    CHEBOKSARY_URMARY("Чебоксары","Урмары"),
    ;
    private final String startLocation;
    private final String finishLocation;

    TripDirection(String startLocation, String finishLocation) {
        this.startLocation = startLocation;
        this.finishLocation = finishLocation;
    }

    public String getFinishLocation() {
        return finishLocation;
    }

    public String getStartLocation() {
        return startLocation;
    }
}
