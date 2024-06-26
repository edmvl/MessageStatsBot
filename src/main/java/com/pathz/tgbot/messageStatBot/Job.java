package com.pathz.tgbot.messageStatBot;

import com.pathz.tgbot.messageStatBot.service.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class Job {

    private final StatsService statsService;
    private final StinkyService stinkyService;
    private final ChallengeService challengeService;
    private final HoroService horoService;
    private final HolidayService holidayService;
    private final RemindService remindService;

    public Job(StatsService statsService, StinkyService stinkyService, ChallengeService challengeService,
               HoroService horoService, HolidayService holidayService,
               RemindService remindService) {
        this.statsService = statsService;
        this.stinkyService = stinkyService;
        this.challengeService = challengeService;
        this.horoService = horoService;
        this.holidayService = holidayService;
        this.remindService = remindService;
    }

    @Scheduled(cron = "55 59 12,23 * * ?")
    public void sendStats() {
        System.out.println("sendStats started at " + LocalDateTime.now());
        statsService.sendStatAllChat();
        System.out.println("sendStats finished at " + LocalDateTime.now());
    }

    @Scheduled(cron = "0 0 10 * * ?")
    public void sendStinky() {
        System.out.println("sendStinky started at " + LocalDateTime.now());
        stinkyService.sendStinkyAllChat();
        System.out.println("sendStinky finished at " + LocalDateTime.now());
    }

    @Scheduled(cron = "1 0 0 * * ?")
    public void loadHoro() {
        System.out.println("loadHoro started at " + LocalDateTime.now());
        horoService.grubDataFromResource();
        System.out.println("loadHoro finished at " + LocalDateTime.now());
    }

    @Scheduled(cron = "0 * 8-23 * * ?")
    public void sendChallenges() {
        challengeService.finishAll();
    }

    @Scheduled(cron = "0 0 8 * * ?")
    public void sendHolidaysAllChat() {
        System.out.println("sendHolidaysAllChat started at " + LocalDateTime.now());
        holidayService.sendHolidaysAllChat();
        System.out.println("sendHolidaysAllChat finished at " + LocalDateTime.now());
    }
    @Scheduled(cron = "0 0 10 * * ?")
    public void sendRemindersAllChat() {
        System.out.println("sendRemindersAllChat started at " + LocalDateTime.now());
        remindService.playReminder();
        System.out.println("sendRemindersAllChat finished at " + LocalDateTime.now());
    }
}
