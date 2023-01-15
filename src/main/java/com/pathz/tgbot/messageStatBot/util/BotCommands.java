package com.pathz.tgbot.messageStatBot.util;

public enum BotCommands {

    HELP_COMMAND("/help", "тем тумалла ку ботпа, ăнлантар-ха"),
    //GET_SILENT_USERS("/шăшисенешыра", "шпиёнсене тупмала, хулăпа хĕнемелле"),
    GET_STINKY_ASS("/pohlo", "кашни кун чатра такам пăсарать, щав пăсараканнине шыраса тупатпăр"),
    GET_CHATTY("/suroh", "хăшĕ перисем чата кĕрес умĕнь сурăх тути çиеççе"),

    GET_STATS_ALL("/stat", "вся статистика"),

    GET_CHATTY_DAYS("/days", "статистика по дням");

    public String getCommand() {
        return command;
    }

    public String getExplainer() {
        return explainer;
    }

    private final String command;
    private final String explainer;

    BotCommands(String command, String explainer) {
        this.command = command;
        this.explainer = explainer;
    }
}
