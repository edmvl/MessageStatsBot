package com.pathz.tgbot.messageStatBot.service;

import com.pathz.tgbot.messageStatBot.entity.Booking;
import com.pathz.tgbot.messageStatBot.entity.Trip;
import com.pathz.tgbot.messageStatBot.message_executor.MessageExecutor;
import com.pathz.tgbot.messageStatBot.repo.BookingRepo;
import com.pathz.tgbot.messageStatBot.repo.TripRepo;
import com.pathz.tgbot.messageStatBot.util.enums.InlineCommand;
import com.pathz.tgbot.messageStatBot.util.enums.TripDirection;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

    public void publishTrip(Long tripId) {
        Optional<Trip> tripOptional = tripRepo.findById(tripId);
        if (tripOptional.isEmpty()) {
            throw new RuntimeException("Trip not found by id");
        }
        Trip trip = tripOptional.get();
        trip.setPublished(true);
        tripRepo.save(trip);
        LocalDateTime dateTime = trip.getDateTime();
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

    public void selectDate(Long chatId) {
        sendInlineKeyboard(chatId, "Выберите дату поездки", getTripDateButtons());
    }

    private List<InlineKeyboardButton> getTripDateButtons() {
        return List.of(
                getInlineKeyboardButton("Сегодня", InlineCommand.SELECT_DATE.getCommand() + ";"  + "today"),
                getInlineKeyboardButton("Завтра", InlineCommand.SELECT_DATE.getCommand() + ";"  + "tomorrow")
        );
    }

    public void selectTime(Long chatId) {
        sendInlineKeyboard(chatId, "Выберите время поездки", getTripTimeButtons());
    }

    private List<InlineKeyboardButton> getTripTimeButtons() {
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();

        return List.of(
                getInlineKeyboardButton("23:00", InlineCommand.SELECT_TIME.getCommand()  + ";" + "23:00")
        );
    }

    public void selectSeats(Long chatId) {
        sendInlineKeyboard(chatId, "Выберите количкство свободных мест", getTripSeatsButtons());
    }

    private List<InlineKeyboardButton> getTripSeatsButtons() {
        return List.of(
                getInlineKeyboardButton("1", InlineCommand.SELECT_SEAT.getCommand() + ";"  + "1"),
                getInlineKeyboardButton("2", InlineCommand.SELECT_SEAT.getCommand() + ";"  + "2"),
                getInlineKeyboardButton("3", InlineCommand.SELECT_SEAT.getCommand() + ";"  + "3"),
                getInlineKeyboardButton("4", InlineCommand.SELECT_SEAT.getCommand() + ";"  + "4"),
                getInlineKeyboardButton("5", InlineCommand.SELECT_SEAT.getCommand() + ";"  + "5"),
                getInlineKeyboardButton("6", InlineCommand.SELECT_SEAT.getCommand() + ";"  + "6")
        );
    }

    public void startTripFlow(Long chatId, Integer messageId) {
        messageExecutor.deleteMessage(chatId, messageId);
        sendInlineKeyboard(chatId, "Выберите направление поездки", getTripDirectionButtons());
    }

    private void sendInlineKeyboard(Long chatId, String text, List<InlineKeyboardButton> tripListButtons) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(new ArrayList<>(tripListButtons));
        inlineKeyboardMarkup.setKeyboard(keyboard);
        SendMessage message = new SendMessage();
        message.setReplyMarkup(inlineKeyboardMarkup);
        message.setChatId(chatId);
        message.setText(text);
        messageExecutor.sendMessage(message);
    }

    private List<InlineKeyboardButton> getTripDirectionButtons() {
        return Arrays.stream(TripDirection.values()).map(this::mapDirections).toList();
    }

    private InlineKeyboardButton mapDirections(TripDirection direction) {
        return getInlineKeyboardButton(
                direction.getStartLocation() + " - " + direction.getFinishLocation(),
                InlineCommand.SELECT_DIRECTION.getCommand() + ";" + direction.name()
        );
    }

    private InlineKeyboardButton getInlineKeyboardButton(String text, String data) {
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(text);
        inlineKeyboardButton.setCallbackData(data);
        return inlineKeyboardButton;
    }
}
