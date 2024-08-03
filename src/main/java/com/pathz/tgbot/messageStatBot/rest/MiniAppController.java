package com.pathz.tgbot.messageStatBot.rest;

import com.pathz.tgbot.messageStatBot.entity.Trip;
import com.pathz.tgbot.messageStatBot.service.TripService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class MiniAppController {

    private final TripService  tripService;

    public MiniAppController(TripService tripService) {
        this.tripService = tripService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/trip-view")
    public String tripView(@PathVariable String id) {
        return "trip-view";
    }

    @GetMapping("/trips")
    public String trips(Model model) {
        List<Trip> tripList = tripService.getTripList();
        model.addAttribute("tripList", tripList);
        return "trips";
    }
}
