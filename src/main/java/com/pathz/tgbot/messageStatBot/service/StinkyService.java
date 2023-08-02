package com.pathz.tgbot.messageStatBot.service;

import com.pathz.tgbot.messageStatBot.entity.Stinky;
import com.pathz.tgbot.messageStatBot.message_executor.MessageExecutor;
import com.pathz.tgbot.messageStatBot.repo.StatsRepo;
import com.pathz.tgbot.messageStatBot.repo.StinkyRepo;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
public class StinkyService {

    private final StinkyRepo stinkyRepo;
    private final StatsRepo statsRepo;
    private final MessageExecutor messageExecutor;

    public StinkyService(StinkyRepo stinkyRepo, StatsRepo statsRepo, MessageExecutor messageExecutor) {
        this.stinkyRepo = stinkyRepo;
        this.statsRepo = statsRepo;
        this.messageExecutor = messageExecutor;
    }

    public Stinky findByMessage(String chatId, LocalDate date) {
        return stinkyRepo.findByChatIdAndDate(chatId, date);
    }

    public void save(String chatId, String userId, LocalDate date) {
        Stinky stinky = new Stinky();
        stinky.setChatId(chatId);
        stinky.setUserId(userId);
        stinky.setDate(date);
        stinkyRepo.save(stinky);
    }

    public String getStinky(String chatId) {
        List<String> distinctUserIdByChatId = statsRepo.findDistinctUserIdByChatId(chatId);
        int i = (int) (Math.random() * distinctUserIdByChatId.size());
        return distinctUserIdByChatId.get(i);
    }

    public String getStinky(String chatId, String userId) {
        List<String> distinctUserIdByChatId = statsRepo.findDistinctUserIdByChatId(chatId);
        int i = (int) (Math.random() * distinctUserIdByChatId.size());
        return distinctUserIdByChatId.get(i);
    }

    public void sendStinky(Long chatId, Integer messageId) {
        sendStinky(chatId);
        messageExecutor.deleteMessage(chatId, messageId);
    }

    public void sendStinky(Long chatId) {
        Stinky existedStinky = findByMessage(chatId.toString(), LocalDate.now());
        SendMessage sendMessage = new SendMessage();
        String text;
        User user = null;
        String stinkyUserId;
        if (Objects.nonNull(existedStinky)) {
            text = "Паянхи шăршлă кута тупнă:\n";
            stinkyUserId = existedStinky.getUserId();
            user = messageExecutor.searchUsersInChat(chatId.toString(), stinkyUserId);
        } else {
            stinkyUserId = getStinky(chatId.toString());
            text = "Кунăн кучĕ питĕ шăршлă:\n";
            int counter = 0;
            while (Objects.isNull(user) || counter < 10) {
                System.out.println("try to get stinky " + (counter + 1));
                stinkyUserId = getStinky(chatId.toString());
                user = messageExecutor.searchUsersInChat(chatId.toString(), stinkyUserId);
                counter++;
            }
            save(chatId.toString(), stinkyUserId, LocalDate.now());
        }
        if (Objects.isNull(user)) {
            return;
        }
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setUser(user);
        messageEntity.setOffset(text.length());
        String userIdentityText = firstName + " " + (Objects.nonNull(lastName) ? lastName : "") + "\n";
        text += userIdentityText;
        messageEntity.setLength(userIdentityText.length());
        messageEntity.setType("text_mention");
        sendMessage.setEntities(List.of(messageEntity));
        sendMessage.setText(text);
        sendMessage.setChatId(chatId);
        messageExecutor.sendMessage(sendMessage);
    }
}
