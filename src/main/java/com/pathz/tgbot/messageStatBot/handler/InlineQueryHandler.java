package com.pathz.tgbot.messageStatBot.handler;

import com.pathz.tgbot.messageStatBot.message_executor.MessageExecutor;
import com.pathz.tgbot.messageStatBot.service.TripService;
import com.pathz.tgbot.messageStatBot.util.enums.InlineCommand;
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
        String callbackQueryData = callbackQuery.getData();
        Long userId = callbackQuery.getFrom().getId();
        Integer messageId = callbackQuery.getMessage().getMessageId();
        Long chatId = callbackQuery.getMessage().getChatId();
        String[] split = callbackQueryData.split(";");
        String command = split[0];
        String data = split[1];
        if (InlineCommand.SELECT_DATE.getPrevStep().equals(command)) {
            tripService.selectDate(chatId);
        }
        if (InlineCommand.SELECT_TIME.getPrevStep().equals(command)) {
            tripService.selectTime(chatId);
        }
        if (InlineCommand.SELECT_SEAT.getPrevStep().equals(command)) {
            tripService.selectSeats(chatId);
        }
        if (InlineCommand.CONFIRM.getPrevStep().equals(command)) {
            tripService.publishTrip(chatId);
        }
        messageExecutor.deleteMessage(chatId, messageId);
        System.out.println(callbackQuery);
    }
}
