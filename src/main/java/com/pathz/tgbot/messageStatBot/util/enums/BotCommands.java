package com.pathz.tgbot.messageStatBot.util.enums;

public enum BotCommands {

    START_COMMAND("/start", ""),
    GET_STINKY_ASS("/pohlo", "кашни кун чатра такам пăсарать, щав пăсараканнине шыраса тупатпăр"),
    GET_CHATTY("/suroh", "хăшĕ перисем чата кĕрес умĕнь сурăх тути çиеççе"),
    GET_STATS_ALL("/stat", "вся статистика"),
    GET_WEEK_STATS("/week", "статистика за неделю"),
    ADD_WORD("/addword", "добавить слово в список удаляемых", true),
    CHANGED_USERS("/history", "История изменений никнеймов пользователей", true),
    HOLIDAYS("/holidays", "Праздники сегодня"),
    GET_CHATTY_DAYS("/days", "статистика по дням"),
    CHALLANGE_START("/challenge", "начать розыгрыш", true),
    CHALLANGE_REGISTRATION("/reg", "зарегистрировться на розыгрыш"),
    TRIP("/trip", "Создать поездку"),
    REMINDER("/rem", "Создать напоминание"),
    QUIZ("/quiz", "Создать напоминание"),
    FIND_NEAREST_TRIP("/triplist", "Найти ближайшие поездки"),
    ;

    public String getCommand() {
        return command;
    }

    public String getExplainer() {
        return explainer;
    }

    public Boolean isForAdmin() {
        return for_admin;
    }

    private final String command;
    private final String explainer;
    private final Boolean for_admin;

    BotCommands(String command, String explainer) {
        this.command = command;
        this.explainer = explainer;
        this.for_admin = Boolean.FALSE;
    }

    BotCommands(String command, String explainer, Boolean for_admin) {
        this.command = command;
        this.explainer = explainer;
        this.for_admin = for_admin;
    }
}
