package com.pathz.tgbot.messageStatBot;

import com.pathz.tgbot.messageStatBot.entity.Stats;
import com.pathz.tgbot.messageStatBot.message_executor.MessageExecutor;
import com.pathz.tgbot.messageStatBot.service.StatsService;
import org.springframework.scheduling.annotation.Scheduled;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Job {

    private final StatsService statsService;
    private final MessageExecutor messageExecutor;

    public Job(StatsService statsService, MessageExecutor messageExecutor) {
        this.statsService = statsService;
        this.messageExecutor = messageExecutor;
    }

    @Scheduled(cron = "55 59 23 * * ?")
    public void sendStats(){
        System.out.println("schedule sendStats at 55 59 23 * * ? started");
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
                MessageEntity messageEntity = new MessageEntity();
                messageEntity.setUser(user);
                messageEntity.setOffset(text.length());
                String userIdentityText = firstName + " " + (Objects.nonNull(lastName) ? lastName : "") + "(" + stats.getCount() + ")" + "\n";
                text.append(userIdentityText);
                messageEntity.setLength(userIdentityText.length());
                messageEntity.setType("text_mention");
                messageEntities.add(messageEntity);
            });
            sendMessage.setEntities(messageEntities);
            sendMessage.setText(text.toString());
            messageExecutor.sendMessage(sendMessage);
        });
        System.out.println("schedule sendStats at 55 59 23 * * ? started");
    }
}
