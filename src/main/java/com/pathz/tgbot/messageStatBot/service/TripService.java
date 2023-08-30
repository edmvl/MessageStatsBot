package com.pathz.tgbot.messageStatBot.service;

import com.pathz.tgbot.messageStatBot.message_executor.MessageExecutor;
import com.pathz.tgbot.messageStatBot.repo.BookingRepo;
import com.pathz.tgbot.messageStatBot.repo.TripRepo;
import org.springframework.stereotype.Service;

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
}
