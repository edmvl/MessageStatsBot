package com.pathz.tgbot.messageStatBot.util.enums;

import java.util.Arrays;
import java.util.List;

public enum InlineCommand {

    SELECT_TRIP_DIRECTION("trip_direction", "Направление поездки"),
    SELECT_TRIP_DATE("trip_date", "Выбор даты"),
    SELECT_TRIP_TIME("trip_time", "Выбор времени"),
    SELECT_TRIP_SEAT("trip_seat", "Выбор свободных мест"),
    TRIP_CONFIRM("trip_confirm", "Подтвердить"),
    ;
    private final String command;
    private final String description;

    InlineCommand(String command, String description) {
        this.command = command;
        this.description = description;
    }

    public String getCommand() {
        return command;
    }

    public String getDescription() {
        return description;
    }

    public String getPrevStep() {
        List<String> inlineCommands = Arrays.stream(values()).map(InlineCommand::getCommand).toList();
        return TRIP_CONFIRM.getCommand().equals(command) ? "" : inlineCommands.get(inlineCommands.indexOf(command) - 1);
    }
}
