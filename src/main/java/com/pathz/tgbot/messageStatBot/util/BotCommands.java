package com.pathz.tgbot.messageStatBot.util;

public enum BotCommands {

    HELP_COMMAND("/help", "тем тумалла ку ботпа, онлантар-ха"),
    //GET_SILENT_USERS("/шошисенешыра", "шпиёнсене тупмала, хулопа хенемелле"),
    GET_STINKY_ASS("/pohlo", "кашни кун чатра такам посарать, щав посараканнине шыраса тупатпор"),
    GET_CHATTY("/suroh", "хоше перисем чата керес умень сурох тути щиещще");

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
