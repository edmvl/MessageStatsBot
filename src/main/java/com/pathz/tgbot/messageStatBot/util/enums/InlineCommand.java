package com.pathz.tgbot.messageStatBot.util.enums;

import java.util.Arrays;
import java.util.List;

public enum InlineCommand {


    SELECT_DIRECTION("direction", "Направление поездки"),
    SELECT_DATE("date", "Выбор даты"),
    SELECT_TIME("time", "Выбор времени"),
    SELECT_SEAT("seat", "Выбор свободных мест"),
    CONFIRM("confirm", "Подтвердить"),
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
        return CONFIRM.getCommand().equals(command) ? "" : inlineCommands.get(inlineCommands.indexOf(command) - 1);
    }
}
