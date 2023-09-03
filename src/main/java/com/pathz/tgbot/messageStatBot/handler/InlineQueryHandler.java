package com.pathz.tgbot.messageStatBot.handler;

import com.pathz.tgbot.messageStatBot.message_executor.MessageExecutor;
import com.pathz.tgbot.messageStatBot.service.TripService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
public class InlineQueryHandler implements Handler<CallbackQuery>{

    private final MessageExecutor messageExecutor;
    private final TripService tripService;

    public InlineQueryHandler(MessageExecutor messageExecutor, TripService tripService) {
        this.messageExecutor = messageExecutor;
        this.tripService = tripService;
    }

    @Override
    public void choose(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
        Long userId = callbackQuery.getFrom().getId();
        Integer messageId = callbackQuery.getMessage().getMessageId();
        Long chatId = callbackQuery.getMessage().getChatId();
        messageExecutor.deleteMessage(chatId, messageId);
        System.out.println(callbackQuery);
    }
}
