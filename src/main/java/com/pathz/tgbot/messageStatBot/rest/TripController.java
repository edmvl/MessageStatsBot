package com.pathz.tgbot.messageStatBot.rest;

import com.pathz.tgbot.messageStatBot.dto.TripDto;
import com.pathz.tgbot.messageStatBot.entity.Trip;
import com.pathz.tgbot.messageStatBot.service.TripService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/trip")
public class TripController {

    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @GetMapping()
    public List<TripDto> getTrips() {
        List<Trip> tripList = tripService.getTripList();
        return tripList.stream().map(trip -> TripDto.builder()
                .dateTime(trip.getDateTime())
                .destination(trip.getDestination())
                .startFrom(trip.getStartFrom())
                .userId(trip.getUserId())
                .seat(trip.getSeat())
                .build()
        ).collect(Collectors.toList());
    }

    @PostMapping()
    public String createTrip(@RequestBody TripDto tripDto) {
        String tripId = tripService.createTrip(tripDto);
        return tripId;
    }
}
