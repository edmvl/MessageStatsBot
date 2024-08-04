package com.pathz.tgbot.messageStatBot.rest;

import com.pathz.tgbot.messageStatBot.entity.Taxi;
import com.pathz.tgbot.messageStatBot.entity.Trip;
import com.pathz.tgbot.messageStatBot.service.TaxiService;
import com.pathz.tgbot.messageStatBot.service.TripService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/app")
public class MiniAppController {

    private final TripService tripService;
    private final TaxiService taxiService;

    public MiniAppController(TripService tripService, TaxiService taxiService) {
        this.tripService = tripService;
        this.taxiService = taxiService;
    }

    @GetMapping("")
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

    @GetMapping("/taxi")
    public String taxi(Model model) {
        List<Taxi> taxiList = taxiService.getTaxiList();
        model.addAttribute("taxiList", taxiList);
        return "taxi";
    }
}
