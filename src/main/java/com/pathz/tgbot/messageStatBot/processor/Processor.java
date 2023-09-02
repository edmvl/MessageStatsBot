package com.pathz.tgbot.messageStatBot.processor;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;

public interface Processor {

    void executeMessage(Message message);

    void executeInline(CallbackQuery callbackQuery);

    default void process(Update update) {
        if (update.hasMessage()) {
            executeMessage(update.getMessage());
        }
        if (update.hasCallbackQuery()) {
            executeInline(update.getCallbackQuery());
        }
    }
}
