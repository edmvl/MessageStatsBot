package com.pathz.tgbot.messageStatBot;

import com.pathz.tgbot.messageStatBot.service.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Job {

    private final StatsService statsService;
    private final StinkyService stinkyService;
    private final ChallengeService challengeService;
    private final HoroService horoService;
    private final HolidayService holidayService;

    public Job(StatsService statsService, StinkyService stinkyService, ChallengeService challengeService,
               HoroService horoService, HolidayService holidayService
    ) {
        this.statsService = statsService;
        this.stinkyService = stinkyService;
        this.challengeService = challengeService;
        this.horoService = horoService;
        this.holidayService = holidayService;
    }

    @Scheduled(cron = "55 59 12,23 * * ?")
    public void sendStats() {
        List<String> chatIds = statsService.findAllChats();
        for (String chatId : chatIds) {
            try {
                statsService.sendChatty(Long.valueOf(chatId));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Scheduled(cron = "0 0 10 * * ?")
    public void sendStinky() {
        List<String> chatIds = statsService.findAllChats();
        for (String chatId : chatIds) {
            try {
                stinkyService.sendStinky(Long.valueOf(chatId));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Scheduled(cron = "1 0 0 * * ?")
    public void loadHoro() {
        try {
            horoService.grubDataFromResource();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "0 * 8-23 * * ?")
    public void sendChallenges() {
        try {
            challengeService.finishAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "0 3 0 * * ?")
    public void sendHolidaysAllChat() {
        try {
            holidayService.sendHolidaysAllChat();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
