package com.pathz.tgbot.messageStatBot.processor;

import com.pathz.tgbot.messageStatBot.handler.MessageHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class DefaultProcessor implements Processor {

    private final MessageHandler messageHandler;

    public DefaultProcessor(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Override
    public void executeMessage(Message message) {
        messageHandler.choose(message);
    }
}
