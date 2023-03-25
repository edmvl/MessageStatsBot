package com.pathz.tgbot.messageStatBot.util;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class MessageFormatter {
    public static String trimNull(String... str) {
        return Arrays.stream(str).map(s -> Objects.nonNull(s) ? s : "").collect(Collectors.joining(" "));
    }

    public static String getDayAddition(int num) {
        int preLastDigit = num % 100 / 10;
        if (preLastDigit == 1) {
            return "дней";
        }
        switch (num % 10) {
            case 1:
                return "день";
            case 2:
            case 3:
            case 4:
                return "дня";
            default:
                return "дней";
        }

    }
}
