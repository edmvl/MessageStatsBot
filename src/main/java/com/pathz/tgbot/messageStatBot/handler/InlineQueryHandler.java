package com.pathz.tgbot.messageStatBot.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
public class InlineQueryHandler implements Handler<CallbackQuery>{
    @Override
    public void choose(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
        System.out.println(callbackQuery);
    }
}
