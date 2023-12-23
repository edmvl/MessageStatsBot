package com.pathz.tgbot.messageStatBot.util.enums;

import java.util.Objects;

public enum InlineCommand {

    TRIP("trip", "Создать поездку", null),
    SELECT_TRIP_DIRECTION("trip_direction", "Направление поездки", null),
    SELECT_TRIP_DATE("trip_date", "Выбор даты", SELECT_TRIP_DIRECTION),
    SELECT_TRIP_TIME("trip_time", "Выбор времени", SELECT_TRIP_DATE),
    SELECT_TRIP_SEAT("trip_seat", "Выбор свободных мест", SELECT_TRIP_TIME),
    TRIP_CONFIRM("trip_confirm", "Подтвердить", SELECT_TRIP_SEAT),
    FIND_NEAREST_TRIP("nearest_trip", "Найти ближайшие поездки", null),
    REGISTER_TO_TRIP("register_to_trip", "Зарегистрироваться на поездку", null),
    ;
    private final String command;
    private final String description;
    private final InlineCommand prev;

    InlineCommand(String command, String description, InlineCommand prev) {
        this.command = command;
        this.description = description;
        this.prev = prev;
    }

    public String getCommand() {
        return command;
    }

    public String getPrevStep() {
        return Objects.nonNull(prev) ? prev.getCommand() : "";
    }
}
