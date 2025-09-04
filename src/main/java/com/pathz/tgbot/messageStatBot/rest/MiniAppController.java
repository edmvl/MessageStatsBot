package com.pathz.tgbot.messageStatBot.rest;

import com.pathz.tgbot.messageStatBot.dto.ChatViewDto;
import com.pathz.tgbot.messageStatBot.entity.Log;
import com.pathz.tgbot.messageStatBot.entity.Taxi;
import com.pathz.tgbot.messageStatBot.entity.Trip;
import com.pathz.tgbot.messageStatBot.service.LogService;
import com.pathz.tgbot.messageStatBot.service.TaxiService;
import com.pathz.tgbot.messageStatBot.service.TripService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.ws.rs.QueryParam;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/app")
public class MiniAppController {

    private final TripService tripService;
    private final TaxiService taxiService;
    private final LogService logService;

    public MiniAppController(TripService tripService, TaxiService taxiService, LogService logService) {
        this.tripService = tripService;
        this.taxiService = taxiService;
        this.logService = logService;
    }

    @GetMapping("")
    public String index() {
        return "index";
    }

    @GetMapping("/trip-view/{id}")
    public String tripView(@PathVariable String id) {
        return "trip-view";
    }

    @GetMapping("/chat/{chatId}")
    public String tripView(
            @PathVariable String chatId,
            @QueryParam("page") String page,
            @QueryParam("count") String count,
            Model model
    ) {
        int currentPage = Integer.parseInt(Objects.nonNull(page) ? page : "0");
        int cnt = Integer.parseInt(Objects.nonNull(count) ? count : "30");
        List<Log> messages = logService.findByChatId(chatId, currentPage, cnt);
        model.addAttribute("messages", messages);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("count", cnt);
        model.addAttribute("chatId", chatId);
        Log log = messages.stream().findFirst().orElseGet(Log::new);
        model.addAttribute("chatName", log.getChatName());
        model.addAttribute("nextPage", currentPage + 1);
        model.addAttribute("prevPage", Math.max(currentPage - 1, 0));
        return "chat";
    }

    @GetMapping("/chats")
    public String chatsView(Model model) {
        List<ChatViewDto> chats = logService.getAllChats();
        model.addAttribute("chats", chats);
        return "chats";
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

    @GetMapping("/rating/{chatId}")
    public String ratingView(
            @PathVariable String chatId,
            @QueryParam("page") String page,
            @QueryParam("count") String count,
            Model model
    ) {
        int currentPage = Integer.parseInt(Objects.nonNull(page) ? page : "0");
        int cnt = Integer.parseInt(Objects.nonNull(count) ? count : "30");
        List<Log> messages = logService.findByChatId(chatId, currentPage, cnt);
        model.addAttribute("messages", messages);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("count", cnt);
        model.addAttribute("chatId", chatId);
        model.addAttribute("nextPage", currentPage + 1);
        model.addAttribute("prevPage", Math.max(currentPage - 1, 0));
        return "rating";
    }

}
