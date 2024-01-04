package com.pathz.tgbot.messageStatBot.service;

import com.pathz.tgbot.messageStatBot.dto.MessageDTO;
import com.pathz.tgbot.messageStatBot.entity.Stinky;
import com.pathz.tgbot.messageStatBot.message_executor.MessageExecutor;
import com.pathz.tgbot.messageStatBot.repo.LogRepo;
import com.pathz.tgbot.messageStatBot.repo.StinkyRepo;
import com.pathz.tgbot.messageStatBot.util.enums.BotCommands;
import com.pathz.tgbot.messageStatBot.util.enums.ChatSettingConstants;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
public class StinkyService implements CommandExecutable {

    private final StinkyRepo stinkyRepo;
    private final LogRepo logRepo;
    private final StatsService statsService;
    private final SettingsService settingsService;
    private final MessageExecutor messageExecutor;

    public StinkyService(StinkyRepo stinkyRepo, LogRepo logRepo, StatsService statsService, SettingsService settingsService, MessageExecutor messageExecutor) {
        this.stinkyRepo = stinkyRepo;
        this.logRepo = logRepo;
        this.statsService = statsService;
        this.settingsService = settingsService;
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
        List<String> distinctUserIdByChatId = logRepo.findDistinctUserIdByChatId(chatId);
        int i = (int) (Math.random() * distinctUserIdByChatId.size());
        return distinctUserIdByChatId.get(i);
    }

    public void sendStinky(Long chatId, Integer messageId) {
        sendStinky(chatId);
        messageExecutor.deleteMessage(chatId, messageId);
    }

    public void sendStinkyAllChat() {
        List<String> chatIds = statsService.findAllChats();
        for (String chatId : chatIds) {
            if (settingsService.isDisabled(chatId, ChatSettingConstants.ENABLE_STINKY)) {
                continue;
            }
            try {
                sendStinky(Long.valueOf(chatId));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sendStinky(Long chatId) {
        Stinky existedStinky = findByMessage(chatId.toString(), LocalDate.now());
        SendMessage sendMessage = new SendMessage();
        String text;
        User user;
        String stinkyUserId;
        if (Objects.nonNull(existedStinky)) {
            text = "Паянхи шăршлă кута тупнă:\n";
            stinkyUserId = existedStinky.getUserId();
            String userNames = String.join(",", logRepo.findLastUserNameByChatId(chatId.toString(), stinkyUserId));
            user = new User(Long.valueOf(stinkyUserId), userNames, false);
        } else {
            text = "Кунăн кучĕ питĕ шăршлă:\n";
            stinkyUserId = getStinky(chatId.toString());
            String userNames = String.join(",", logRepo.findLastUserNameByChatId(chatId.toString(), stinkyUserId));
            user = new User(Long.valueOf(stinkyUserId), userNames, false);
            save(chatId.toString(), stinkyUserId, LocalDate.now());
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
    @Override
    public void executeCommand(MessageDTO messageDTO) {
        if (messageDTO.getUserText().startsWith(BotCommands.GET_STINKY_ASS.getCommand())) {
            sendStinky(messageDTO.getChatId(), messageDTO.getMessageId());
        }
    }
}
