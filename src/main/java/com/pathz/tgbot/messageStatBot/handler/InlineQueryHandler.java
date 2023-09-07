package com.pathz.tgbot.messageStatBot.handler;

import com.pathz.tgbot.messageStatBot.message_executor.MessageExecutor;
import com.pathz.tgbot.messageStatBot.service.TripService;
import com.pathz.tgbot.messageStatBot.util.enums.InlineCommand;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
public class InlineQueryHandler implements Handler<CallbackQuery> {

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
        String id = split[2];

        //-----------Trip Actions-------
        if (InlineCommand.SELECT_TRIP_DIRECTION.getCommand().equals(command)) {
            id = tripService.createTrip(userId, data);
        }
        if (InlineCommand.SELECT_TRIP_DATE.getCommand().equals(command)) {
            tripService.updateDate(id, data);
        }
        if (InlineCommand.SELECT_TRIP_TIME.getCommand().equals(command)) {
            tripService.updateTime(id, data);
        }
        if (InlineCommand.SELECT_TRIP_SEAT.getCommand().equals(command)) {
            tripService.updateSeat(id, data);
        }
        //----------Trip Flow----------
        if (InlineCommand.SELECT_TRIP_DATE.getPrevStep().equals(command)) {
            tripService.selectDate(chatId, id);
        }
        if (InlineCommand.SELECT_TRIP_TIME.getPrevStep().equals(command)) {
            tripService.selectTime(chatId, id);
        }
        if (InlineCommand.SELECT_TRIP_SEAT.getPrevStep().equals(command)) {
            tripService.selectSeats(chatId, id);
        }
        if (InlineCommand.TRIP_CONFIRM.getPrevStep().equals(command)) {
            tripService.publishTrip(chatId, id);
        }
        messageExecutor.deleteMessage(chatId, messageId);
        System.out.println(callbackQuery);
    }
}
