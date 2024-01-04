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
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.pathz.tgbot.messageStatBot.util.enums.InlineCommand.*;

@Service
public class TripService implements CommandExecutable {
    private final MessageExecutor messageExecutor;
    private final TripRepo tripRepo;
    private final BookingRepo bookingRepo;

    private final SettingsService settingsService;

    public TripService(MessageExecutor messageExecutor, TripRepo tripRepo, BookingRepo bookingRepo, SettingsService settingsService) {
        this.messageExecutor = messageExecutor;
        this.tripRepo = tripRepo;
        this.bookingRepo = bookingRepo;
        this.settingsService = settingsService;
    }

    public void findNearestTrip(Long chatId) {
        sendInlineKeyboard(chatId, "Ближайшие поездки:", getTripListButtons(), 1);
    }

    private List<InlineKeyboardButton> getTripListButtons() {
        LocalDateTime now = LocalDateTime.now();
        List<Trip> allByDateTimeBetweenAndPublished = tripRepo.findAllByDateTimeBetweenAndPublishedOrderByDateTimeAsc(now.withHour(0), now.plusHours(24), true);
        return allByDateTimeBetweenAndPublished.stream()
                .map(trip -> getInlineKeyboardButton(
                        trip.getStartFrom() + " - " + trip.getDestination() + "\n" +
                                MessageFormatter.formatTripDate(trip.getDateTime()) + "\n" +
                                MessageFormatter.formatTripTime(trip.getDateTime()) + "\n" +
                                "мест: " + trip.getSeat(),
                        REGISTER_TO_TRIP.getCommand() + ";" + " " + ";" + trip.getId())
                )
                .collect(Collectors.toList());
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

    public void confirmTripParams(Long chatId, String id) {
        Long tripId = Long.valueOf(id);
        Optional<Trip> tripOptional = tripRepo.findById(tripId);
        if (tripOptional.isEmpty()) {
            throw new RuntimeException("Trip not found by id");
        }
        StringBuilder mess = new StringBuilder();
        Trip trip = tripOptional.get();
        mess.append("Параметры поездки\n");
        mess.append("\uD83D\uDEE3 ").append(trip.getStartFrom()).append("-").append(trip.getDestination()).append("\n");
        mess.append("\uD83D\uDCC5 Дата: ").append(MessageFormatter.formatTripDate(trip.getDateTime())).append("\n");
        mess.append("⌚ Время: ").append(MessageFormatter.formatTripTime(trip.getDateTime())).append("\n");
        mess.append("✅ Свободных мест: ").append(trip.getSeat()).append("\n");
        mess.append("Подтвердить?");
        sendInlineKeyboard(chatId, mess.toString(), getTripConfirmButtons(id), 6);
    }

    public void publishTrip(String id, String data) {
        Optional<Trip> tripOptional = tripRepo.findById(Long.valueOf(id));
        if (tripOptional.isEmpty()) {
            throw new RuntimeException("Trip not found by id");
        }
        if (!"yes".equals(data)) {
            return;
        }
        Trip trip = tripOptional.get();
        trip.setPublished(true);
        tripRepo.save(trip);
        publishToChannel(trip);
    }

    private void publishToChannel(Trip trip) {
        String tripChannelId = settingsService.findTripChannelId();
        SendMessage message = new SendMessage();
        String mess = "#Попутка_" + trip.getStartFrom() + "_" + trip.getDestination() + "\n" +
                "\uD83D\uDEE3 " + trip.getStartFrom() + "-" + trip.getDestination() + "\n" +
                "\uD83D\uDCC5 Дата: " + MessageFormatter.formatTripDate(trip.getDateTime()) + "\n" +
                "⌚ Время: " + MessageFormatter.formatTripTime(trip.getDateTime()) + "\n" +
                "✅ Свободных мест: " + trip.getSeat() + "\n" +
                "Связь: ";
        message.setChatId(tripChannelId);
        String userId = trip.getUserId();
        User user = new User(Long.valueOf(userId), "ЛС", false);
        MessageEntity messageEntity = new MessageEntity();
        String userIdentity = user.getFirstName();
        messageEntity.setOffset(mess.length());
        messageEntity.setType("text_mention");
        messageEntity.setLength(userIdentity.length());
        messageEntity.setUser(user);
        mess = mess + userIdentity;
        message.setText(mess);
        message.setEntities(List.of(messageEntity));
        messageExecutor.sendMessage(message);
    }

    public void selectTripDirection(Long chatId) {
        selectTripDirection(chatId, null);
    }

    public void selectTripDirection(Long chatId, Long tripId) {
        sendInlineKeyboard(chatId, "Выберите направление поездки", getTripDirectionButtons(tripId), 6);
    }

    public void selectDate(Long chatId, String tripId) {
        sendInlineKeyboard(chatId, "Выберите дату поездки", getTripDateButtons(tripId), 6);
    }

    public void selectTime(Long chatId, String tripId) {
        sendInlineKeyboard(chatId, "Выберите время поездки", getTripTimeButtons(tripId), 6);
    }

    public void selectSeats(Long chatId, String tripId) {
        sendInlineKeyboard(chatId, "Выберите количкство свободных мест", getTripSeatsButtons(tripId), 6);
    }

    private List<InlineKeyboardButton> getTripDateButtons(String tripId) {
        return List.of(
                getInlineKeyboardButton("Сегодня", SELECT_TRIP_DATE.getCommand() + ";" + "today" + ";" + tripId),
                getInlineKeyboardButton("Завтра", SELECT_TRIP_DATE.getCommand() + ";" + "tomorrow" + ";" + tripId)
        );
    }

    private List<InlineKeyboardButton> getTripConfirmButtons(String tripId) {
        return List.of(
                getInlineKeyboardButton("Да", TRIP_CONFIRM.getCommand() + ";" + "yes" + ";" + tripId),
                getInlineKeyboardButton("Нет", TRIP_CONFIRM.getCommand() + ";" + "no" + ";" + tripId)
        );
    }

    private List<InlineKeyboardButton> getTripTimeButtons(String tripId) {
        List<InlineKeyboardButton> result = new ArrayList<>();
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        var ticker = isTripToday(tripId) ? LocalDateTime.now().plusHours(2).withMinute(0) : LocalDate.now().atStartOfDay();
        while (ticker.isBefore(tomorrow.atStartOfDay())) {
            String text = ticker.getHour() + ":" + ticker.getMinute();
            result.add(
                    getInlineKeyboardButton(
                            text,
                            SELECT_TRIP_TIME.getCommand() + ";" + text + ";" + tripId
                    )
            );
            ticker = ticker.plusMinutes(30);
        }
        return result;
    }

    private boolean isTripToday(String tripId) {
        Optional<Trip> byId = tripRepo.findById(Long.valueOf(tripId));
        return byId.filter(trip -> !LocalDate.now().isBefore(LocalDate.from(trip.getDateTime()))).isPresent();
    }

    private List<InlineKeyboardButton> getTripSeatsButtons(String tripId) {
        List<InlineKeyboardButton> result = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            result.add(getInlineKeyboardButton(String.valueOf(i), SELECT_TRIP_SEAT.getCommand() + ";" + i + ";" + tripId));
        }
        return result;
    }

    private void sendInlineKeyboard(Long chatId, String text, List<InlineKeyboardButton> tripListButtons, int chunkSize) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(prepareChunks(tripListButtons, chunkSize));
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

    private <T> List<List<T>> prepareChunks(List<T> inputList, int chunkSize) {
        AtomicInteger counter = new AtomicInteger();
        return inputList.stream().collect(Collectors.groupingBy(it -> counter.getAndIncrement() / chunkSize)).values().stream().toList();
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

    public void updateDate(String tripId, String data) {
        Optional<Trip> byId = tripRepo.findById(Long.valueOf(tripId));
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
            date = date.withHour(Integer.parseInt(split[0])).withMinute(Integer.parseInt(split[1]));
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
        if (messageDTO.getUserText().equals(BotCommands.TRIP.getCommand())) {
            messageExecutor.deleteMessage(messageDTO.getChatId(), messageDTO.getMessageId());
            selectTripDirection(messageDTO.getChatId());
        }
        if (messageDTO.getUserText().equals(BotCommands.FIND_NEAREST_TRIP.getCommand())) {
            messageExecutor.deleteMessage(messageDTO.getChatId(), messageDTO.getMessageId());
            findNearestTrip(messageDTO.getChatId());
        }
    }
}
