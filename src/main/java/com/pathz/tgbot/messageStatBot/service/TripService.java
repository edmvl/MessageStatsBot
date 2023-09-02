package com.pathz.tgbot.messageStatBot.service;

import com.pathz.tgbot.messageStatBot.entity.Booking;
import com.pathz.tgbot.messageStatBot.entity.Trip;
import com.pathz.tgbot.messageStatBot.message_executor.MessageExecutor;
import com.pathz.tgbot.messageStatBot.repo.BookingRepo;
import com.pathz.tgbot.messageStatBot.repo.TripRepo;
import com.pathz.tgbot.messageStatBot.util.enums.TripDirection;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TripService {
    private final MessageExecutor messageExecutor;
    private final TripRepo tripRepo;
    private final BookingRepo bookingRepo;

    public TripService(MessageExecutor messageExecutor, TripRepo tripRepo, BookingRepo bookingRepo) {
        this.messageExecutor = messageExecutor;
        this.tripRepo = tripRepo;
        this.bookingRepo = bookingRepo;
    }

    public void showNearestTrip() {

    }

    public void registerToTrip(Long tripId, String userId, String startFrom, String destination, int seat) {
        Booking booking = new Booking();
        booking.setTripId(tripId);
        booking.setUserId(userId);
        booking.setStartFrom(startFrom);
        booking.setDestination(destination);
        booking.setSeat(seat);
        bookingRepo.save(booking);
    }

    public void publishBooking() {

    }

    public void publishTrip(LocalDateTime dateTime, String userId, TripDirection tripDirection, Integer seat) {
        Trip trip = new Trip();
        trip.setDateTime(dateTime);
        trip.setUserId(userId);
        trip.setDestination(tripDirection.getFinishLocation());
        trip.setStartFrom(tripDirection.getStartLocation());
        trip.setSeat(seat);
        tripRepo.save(trip);
        List<Booking> bookingForInform = bookingRepo.findAllByDateTimeBetweenAndAccepted(
                dateTime.minusHours(2), dateTime.plusHours(2), false
        );
        bookingForInform.stream().map(Booking::getUserId).forEach(u -> {
            SendMessage message = new SendMessage();
            message.setText("Появились поездки на выбранную вами дату");
            message.setChatId(u);
            messageExecutor.sendMessage(message);
        });
    }

    public void startTripFlow(Long chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();
        inlineKeyboardButtons.add(getInlineKeyboardButton(TripDirection.CHEBOKSARY_URMARY));
        inlineKeyboardButtons.add(getInlineKeyboardButton(TripDirection.URMARY_CHEBOKSARY));
        keyboard.add(inlineKeyboardButtons);
        inlineKeyboardMarkup.setKeyboard(keyboard);
        SendMessage message = new SendMessage();
        message.setReplyMarkup(inlineKeyboardMarkup);
        message.setChatId(chatId);
        message.setText("Выберите направление поездки");
        messageExecutor.sendMessage(message);
    }

    private InlineKeyboardButton getInlineKeyboardButton(TripDirection tripDirection) {
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(tripDirection.getStartLocation() + " - " + tripDirection.getFinishLocation());
        inlineKeyboardButton.setCallbackData(tripDirection.name());
        return inlineKeyboardButton;
    }
}
