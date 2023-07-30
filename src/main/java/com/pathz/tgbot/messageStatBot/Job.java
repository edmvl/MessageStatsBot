package com.pathz.tgbot.messageStatBot;

import com.pathz.tgbot.messageStatBot.service.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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
        System.out.println("sendStats started at " + LocalDateTime.now());
        List<String> chatIds = statsService.findAllChats();
        for (String chatId : chatIds) {
            try {
                statsService.sendChatty(Long.valueOf(chatId));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("sendStats finished at " + LocalDateTime.now());
    }

    @Scheduled(cron = "0 0 10 * * ?")
    public void sendStinky() {
        System.out.println("sendStinky started at " + LocalDateTime.now());
        List<String> chatIds = statsService.findAllChats();
        for (String chatId : chatIds) {
            try {
                stinkyService.sendStinky(Long.valueOf(chatId));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("sendStinky finished at " + LocalDateTime.now());
    }

    @Scheduled(cron = "1 0 0 * * ?")
    public void loadHoro() {
        System.out.println("loadHoro started at " + LocalDateTime.now());
        try {
            horoService.grubDataFromResource();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("loadHoro finished at " + LocalDateTime.now());
    }

    @Scheduled(cron = "0 * 8-23 * * ?")
    public void sendChallenges() {
        System.out.println("sendChallenges started at " + LocalDateTime.now());
        try {
            challengeService.finishAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("sendChallenges finished at " + LocalDateTime.now());
    }

    @Scheduled(cron = "0 3 0 * * ?")
    public void sendHolidaysAllChat() {
        System.out.println("sendHolidaysAllChat started at " + LocalDateTime.now());
        try {
            holidayService.sendHolidaysAllChat();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("sendHolidaysAllChat finished at " + LocalDateTime.now());
    }
}
