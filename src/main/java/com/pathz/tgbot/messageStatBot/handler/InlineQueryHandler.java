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
        String tripId = split[2];
        tripId = executeTripAction(userId, chatId, command, data, tripId);
        executeTripFlow(chatId, command, tripId);
        messageExecutor.deleteMessage(chatId, messageId);
        System.out.println(callbackQuery);
    }

    private String executeTripAction(Long userId, Long chatId, String command, String data, String tripId) {
        if (InlineCommand.TRIP.getCommand().equals(command)) {
            tripService.selectTripDirection(chatId);
        }
        if (InlineCommand.SELECT_TRIP_DIRECTION.getCommand().equals(command)) {
            tripId = tripService.createTrip(userId, data);
        }
        if (InlineCommand.SELECT_TRIP_DATE.getCommand().equals(command)) {
            tripService.updateDate(tripId, data);
        }
        if (InlineCommand.SELECT_TRIP_TIME.getCommand().equals(command)) {
            tripService.updateTime(tripId, data);
        }
        if (InlineCommand.SELECT_TRIP_SEAT.getCommand().equals(command)) {
            tripService.updateSeat(tripId, data);
        }
        if (InlineCommand.TRIP_CONFIRM.getCommand().equals(command)) {
            tripService.publishTrip(tripId, data);
        }
        if (InlineCommand.FIND_NEAREST_TRIP.getCommand().equals(command)) {
            tripService.findNearestTrip(chatId);
        }
        if (InlineCommand.FIND_NEAREST_TRIP.getCommand().equals(command)) {
            tripService.findNearestTrip(chatId);
        }
        return tripId;
    }

    private void executeTripFlow(Long chatId, String command, String tripId) {
        if (InlineCommand.SELECT_TRIP_DATE.getPrevStep().equals(command)) {
            tripService.selectDate(chatId, tripId);
        }
        if (InlineCommand.SELECT_TRIP_TIME.getPrevStep().equals(command)) {
            tripService.selectTime(chatId, tripId);
        }
        if (InlineCommand.SELECT_TRIP_SEAT.getPrevStep().equals(command)) {
            tripService.selectSeats(chatId, tripId);
        }
        if (InlineCommand.TRIP_CONFIRM.getPrevStep().equals(command)) {
            tripService.confirmTripParams(chatId, tripId);
        }
    }
}
