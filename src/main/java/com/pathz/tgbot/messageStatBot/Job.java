package com.pathz.tgbot.messageStatBot;

import com.pathz.tgbot.messageStatBot.message_executor.MessageExecutor;
import com.pathz.tgbot.messageStatBot.service.ChallengeService;
import com.pathz.tgbot.messageStatBot.service.HoroService;
import com.pathz.tgbot.messageStatBot.service.StatsService;
import com.pathz.tgbot.messageStatBot.service.StinkyService;
import com.pathz.tgbot.messageStatBot.util.MessageFormatter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.LocalDate;
import java.util.List;

@Component
public class Job {

    private final StatsService statsService;
    private final StinkyService stinkyService;
    private final ChallengeService challengeService;
    private final MessageExecutor messageExecutor;
    private final HoroService horoService;

    public Job(StatsService statsService, StinkyService stinkyService, ChallengeService challengeService, MessageExecutor messageExecutor, HoroService horoService) {
        this.statsService = statsService;
        this.stinkyService = stinkyService;
        this.challengeService = challengeService;
        this.messageExecutor = messageExecutor;
        this.horoService = horoService;
    }

    @Scheduled(cron = "55 59 12,23 * * ?")
    public void sendStats() {
        List<String> chatIds = statsService.findAllChats();
        chatIds.forEach(chatId -> {
            statsService.sendChatty(Long.valueOf(chatId));
        });
    }

    @Scheduled(cron = "0 0 10 * * ?")
    public void sendStinky() {
        List<String> chatIds = statsService.findAllChats();
        chatIds.forEach(chatId -> {
            stinkyService.sendStinky(Long.valueOf(chatId));
        });
    }

    @Scheduled(cron = "1 0 0 * * ?")
    public void loadHoro() {
        horoService.grubDataFromResource();
    }

    @Scheduled(cron = "0 48 10,22 * * ?")
    public void sendSpringReminder() {
        List<String> chatIds = statsService.findAllChats();
        chatIds.forEach(chatId -> {
            LocalDate date = LocalDate.of(2023, 6, 1);
            LocalDate currentDate = LocalDate.now();
            SendMessage sendMessage = new SendMessage();
            long count = currentDate.datesUntil(date).count();
            String text = "До лета осталось " + count + " " + MessageFormatter.getDayAddition((int) count);
            sendMessage.setChatId(chatId);
            sendMessage.setText(text);
            messageExecutor.sendMessage(sendMessage);
        });
    }

    @Scheduled(cron = "0 * 8-23 * * ?")
    public void sendChallenges() {
        challengeService.finishAll();
    }
}
