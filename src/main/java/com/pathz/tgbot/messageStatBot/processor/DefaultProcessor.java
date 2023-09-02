package com.pathz.tgbot.messageStatBot.processor;

import com.pathz.tgbot.messageStatBot.handler.InlineQueryHandler;
import com.pathz.tgbot.messageStatBot.handler.MessageHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;

@Component
public class DefaultProcessor implements Processor {

    private final MessageHandler messageHandler;
    private final InlineQueryHandler inlineQueryHandler;

    public DefaultProcessor(MessageHandler messageHandler, InlineQueryHandler inlineQueryHandler) {
        this.messageHandler = messageHandler;
        this.inlineQueryHandler = inlineQueryHandler;
    }

    @Override
    public void executeMessage(Message message) {
        messageHandler.choose(message);
    }

    @Override
    public void executeInline(CallbackQuery callbackQuery) {
        inlineQueryHandler.choose(callbackQuery);
    }
}
