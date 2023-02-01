package com.pathz.tgbot.messageStatBot;

import com.pathz.tgbot.messageStatBot.entity.Stats;
import com.pathz.tgbot.messageStatBot.entity.Stinky;
import com.pathz.tgbot.messageStatBot.message_executor.MessageExecutor;
import com.pathz.tgbot.messageStatBot.service.StatsService;
import com.pathz.tgbot.messageStatBot.service.StinkyService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
public class Job {

    private final StatsService statsService;
    private final StinkyService stinkyService;
    private final MessageExecutor messageExecutor;

    public Job(StatsService statsService, StinkyService stinkyService, MessageExecutor messageExecutor) {
        this.statsService = statsService;
        this.stinkyService = stinkyService;
        this.messageExecutor = messageExecutor;
    }

    @Scheduled(cron = "55 59 12,23 * * ?")
    public void sendStats() {
        List<String> allChats = statsService.findAllChats();
        allChats.forEach(chatId -> {
            statsService.sendChatty(Long.valueOf(chatId));
        });
    }

    @Scheduled(cron = "0 0 10 * * ?")
    public void sendStinky() {
        List<String> allChats = statsService.findAllChats();
        allChats.forEach(chatId -> {
            stinkyService.sendStinky(Long.valueOf(chatId));
        });
    }

    @Scheduled(cron = "0 48 10,22 * * ?")
    public void sendSpringReminder() {
        String chatId = "-1001868766001";
        LocalDate date = LocalDate.of(2023, 3, 1);
        LocalDate currentDate = LocalDate.now();
        SendMessage sendMessage = new SendMessage();
        String text = "До весны осталось " + currentDate.datesUntil(date).count() + " дней";
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        messageExecutor.sendMessage(sendMessage);
    }
}
