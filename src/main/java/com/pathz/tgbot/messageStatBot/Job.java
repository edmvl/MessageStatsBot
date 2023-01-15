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
            List<Stats> top = statsService.getTop10ChattyUserId(chatId);
            String caption = "Сурăх тути çиекеннисем:\n";
            SendMessage sendMessage = new SendMessage();
            StringBuilder text = new StringBuilder();
            text.append(caption);
            ArrayList<MessageEntity> messageEntities = new ArrayList<>();
            top.forEach(stats -> {
                User user = messageExecutor.searchUsersInChat(chatId, stats.getUserId()).getUser();
                String firstName = user.getFirstName();
                String lastName = user.getLastName();
                sendMessage.setChatId(chatId);
                StatsService.addMessageEntity(text, messageEntities, stats, user, firstName, lastName);
            });
            sendMessage.setEntities(messageEntities);
            sendMessage.setText(text.toString());
            messageExecutor.sendMessage(sendMessage);
        });
    }

    @Scheduled(cron = "0 0 10 * * ?")
    public void sendStinky() {
        List<String> allChats = statsService.findAllChats();
        allChats.forEach(chatId -> {
            stinkyService.sendStinky(Long.valueOf(chatId));
        });
    }
}
