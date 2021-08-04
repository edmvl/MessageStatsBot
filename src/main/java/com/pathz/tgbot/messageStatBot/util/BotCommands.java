package com.pathz.tgbot.messageStatBot.util;

import java.util.HashSet;
import java.util.Set;

public final class BotCommands {

    public static final String STATS_COMMAND = "/msg_stat";
    public static final String GET_MOST_FREQ_WORD_COMMAND = "/top_word";
    public static final String GET_AUTHORS_COMMAND = "/authors";
    public static final String HELP_COMMAND = "/help";

    public static Set<String> commands = new HashSet<>();

    static {
        commands.add(STATS_COMMAND);
        commands.add(GET_MOST_FREQ_WORD_COMMAND);
        commands.add(GET_AUTHORS_COMMAND);
        commands.add(HELP_COMMAND);
    }
}
