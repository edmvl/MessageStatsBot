package com.pathz.tgbot.messageStatBot.service;

import com.pathz.tgbot.messageStatBot.dto.MessageDTO;
import com.pathz.tgbot.messageStatBot.entity.Booking;
import com.pathz.tgbot.messageStatBot.entity.Trip;
import com.pathz.tgbot.messageStatBot.message_executor.MessageExecutor;
import com.pathz.tgbot.messageStatBot.repo.BookingRepo;
import com.pathz.tgbot.messageStatBot.repo.TripRepo;
import com.pathz.tgbot.messageStatBot.util.MessageFormatter;
import com.pathz.tgbot.messageStatBot.util.enums.BotCommands;
import com.pathz.tgbot.messageStatBot.util.enums.TripDirection;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.pathz.tgbot.messageStatBot.util.enums.InlineCommand.*;

@Service
public class TripService implements CommandExecutable {
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

    public void publishTrip(Long tripId, String id) {
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

    public void selectDirection(Long chatId) {
        selectDirection(chatId, null);
    }

    public void selectDirection(Long chatId, Long tripId) {
        sendInlineKeyboard(chatId, "Выберите направление поездки", getTripDirectionButtons(tripId));
    }

    public void selectDate(Long chatId, String id) {
        sendInlineKeyboard(chatId, "Выберите дату поездки", getTripDateButtons(id));
    }

    public void selectTime(Long chatId, String id) {
        sendInlineKeyboard(chatId, "Выберите время поездки", getTripTimeButtons(id));
    }

    public void selectSeats(Long chatId, String id) {
        sendInlineKeyboard(chatId, "Выберите количкство свободных мест", getTripSeatsButtons(id));
    }

    private List<InlineKeyboardButton> getTripDateButtons(String id) {
        return List.of(
                getInlineKeyboardButton("Сегодня", SELECT_TRIP_DATE.getCommand() + ";" + "today" + ";" + id),
                getInlineKeyboardButton("Завтра", SELECT_TRIP_DATE.getCommand() + ";" + "tomorrow" + ";" + id)
        );
    }

    private List<InlineKeyboardButton> getTripTimeButtons(String id) {
        List<InlineKeyboardButton> result = new ArrayList<>();
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        var ticker = isTripToday(id) ? LocalDateTime.now().plusHours(2).withMinute(0) : LocalDate.now().atStartOfDay();
        while (ticker.isBefore(tomorrow.atStartOfDay())) {
            String text = ticker.getHour() + ":" + ticker.getMinute();
            result.add(
                    getInlineKeyboardButton(
                            text,
                            SELECT_TRIP_TIME.getCommand() + ";" + text + ";" + id
                    )
            );
            ticker = ticker.plusMinutes(30);
        }
        return result;
    }

    private boolean isTripToday(String id) {
        Optional<Trip> byId = tripRepo.findById(Long.valueOf(id));
        return byId.filter(trip -> !LocalDate.now().isBefore(LocalDate.from(trip.getDateTime()))).isPresent();
    }

    private List<InlineKeyboardButton> getTripSeatsButtons(String id) {
        List<InlineKeyboardButton> result = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            result.add(getInlineKeyboardButton(String.valueOf(i), SELECT_TRIP_SEAT.getCommand() + ";" + i + ";" + id));
        }
        return result;
    }

    private void sendInlineKeyboard(Long chatId, String text, List<InlineKeyboardButton> tripListButtons) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(prepareChunks(tripListButtons));
        SendMessage message = new SendMessage();
        message.setReplyMarkup(inlineKeyboardMarkup);
        message.setChatId(chatId);
        message.setText(text);
        messageExecutor.sendMessage(message);
    }

    private List<InlineKeyboardButton> getTripDirectionButtons(Long tripId) {
        return Arrays.stream(TripDirection.values()).map(direction -> mapDirections(direction, tripId)).toList();
    }

    private InlineKeyboardButton mapDirections(TripDirection direction, Long tripId) {
        return getInlineKeyboardButton(
                direction.getStartLocation() + " - " + direction.getFinishLocation(),
                SELECT_TRIP_DIRECTION.getCommand() + ";" + direction.name() + ";" + tripId
        );
    }

    private InlineKeyboardButton getInlineKeyboardButton(String text, String data) {
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(text);
        inlineKeyboardButton.setCallbackData(data);
        return inlineKeyboardButton;
    }

    private <T> List<List<T>> prepareChunks(List<T> inputList) {
        AtomicInteger counter = new AtomicInteger();
        return inputList.stream().collect(Collectors.groupingBy(it -> counter.getAndIncrement() / 6)).values().stream().toList();
    }

    public String createTrip(Long userId, String direction) {
        TripDirection tripDirection = TripDirection.valueOf(direction);
        Trip trip = new Trip();
        trip.setUserId(String.valueOf(userId));
        trip.setStartFrom(tripDirection.getStartLocation());
        trip.setDestination(tripDirection.getFinishLocation());
        Trip save = tripRepo.save(trip);
        return save.getId().toString();
    }

    public void updateDate(String id, String data) {
        Optional<Trip> byId = tripRepo.findById(Long.valueOf(id));
        if (byId.isPresent()) {
            LocalDate date = LocalDate.now();
            Trip trip = byId.get();
            if (data.equals("tomorrow")) {
                date = date.plusDays(1);
            }
            trip.setDateTime(date.atStartOfDay());
            tripRepo.save(trip);
        }
    }

    public void updateTime(String id, String data) {
        Optional<Trip> byId = tripRepo.findById(Long.valueOf(id));
        if (byId.isPresent()) {
            Trip trip = byId.get();
            LocalDateTime date = trip.getDateTime();
            String[] split = data.split(":");
            date = date.withHour(Integer.parseInt(split[0]));
            date = date.withMinute(Integer.parseInt(split[1]));
            trip.setDateTime(date);
            tripRepo.save(trip);
        }
    }

    public void updateSeat(String id, String data) {
        Optional<Trip> byId = tripRepo.findById(Long.valueOf(id));
        if (byId.isPresent()) {
            Trip trip = byId.get();
            trip.setSeat(Integer.parseInt(data));
            tripRepo.save(trip);
        }
    }
    @Override
    public void executeCommand(MessageDTO messageDTO) {
        if (messageDTO.getUserText().startsWith(BotCommands.TRIP.getCommand())) {
            messageExecutor.deleteMessage(messageDTO.getChatId(), messageDTO.getMessageId());
            selectDirection(messageDTO.getChatId());
        }
    }
}
