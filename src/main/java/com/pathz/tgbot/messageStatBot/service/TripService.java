package com.pathz.tgbot.messageStatBot.service;

import com.pathz.tgbot.messageStatBot.entity.Booking;
import com.pathz.tgbot.messageStatBot.entity.Trip;
import com.pathz.tgbot.messageStatBot.message_executor.MessageExecutor;
import com.pathz.tgbot.messageStatBot.repo.BookingRepo;
import com.pathz.tgbot.messageStatBot.repo.TripRepo;
import com.pathz.tgbot.messageStatBot.util.enums.TripDirection;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.LocalDateTime;
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
}
