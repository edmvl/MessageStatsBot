package com.pathz.tgbot.messageStatBot.util;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class MessageFormatter {
    public static String trimNull(String ...str) {
        return Arrays.stream(str).map(s -> Objects.nonNull(s) ? s : "").collect(Collectors.joining(" "));
    }
}
