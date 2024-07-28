package com.pathz.tgbot.messageStatBot.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class MiniAppController {
    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/trip-create")
    public String tripCreate() {
        return "trip-create";
    }

    @GetMapping("/trip-view")
    public String tripView(@PathVariable String id) {
        return "trip-view";
    }

    @GetMapping("/trips")
    public String trips() {
        return "trips";
    }
}
