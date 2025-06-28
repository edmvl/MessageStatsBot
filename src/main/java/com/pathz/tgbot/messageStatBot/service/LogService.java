package com.pathz.tgbot.messageStatBot.service;

import com.pathz.tgbot.messageStatBot.dto.MessageDTO;
import com.pathz.tgbot.messageStatBot.entity.Log;
import com.pathz.tgbot.messageStatBot.message_executor.MessageExecutor;
import com.pathz.tgbot.messageStatBot.repo.LogRepo;
import com.pathz.tgbot.messageStatBot.util.enums.BotCommands;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Service
@lombok.extern.java.Log
public class LogService implements CommandExecutable {

    private final LogRepo logRepo;

    private final MessageExecutor messageExecutor;

    public LogService(LogRepo logRepo, MessageExecutor messageExecutor) {
        this.logRepo = logRepo;
        this.messageExecutor = messageExecutor;
    }

    public void save(
            String chatId, String chatName, String userId, String userName, LocalDateTime dateTime,
            String text, Pair<String, String> file) {
        log.log(Level.INFO, String.format("%s %s %s %s", chatName, userName, text, file));
        Log log = new Log();
        log.setChatId(chatId);
        log.setChatName(chatName);
        log.setUserId(userId);
        log.setUserName(userName);
        log.setDateTime(dateTime);
        log.setText(text);
        if (Objects.nonNull(file)) {
            log.setFileType(file.getFirst());
            log.setFile(file.getSecond());
        }
        logRepo.save(log);
    }

    public List<Log> findByChatId(String chatId) {
        return  logRepo.getLogByChatIdAndDateTimeBetween(chatId, LocalDateTime.now().minusDays(1), LocalDateTime.now());
    }

    public void sendChanged(Long chatId, String userId) {
        List<String> userChangedHistoryByChatId = logRepo.findUserChangedHistoryByChatId(String.valueOf(chatId), userId);
        String collect = userChangedHistoryByChatId.stream().map(s -> s + "\n").collect(Collectors.joining("===================\n"));
        messageExecutor.sendMessage(chatId, collect);
    }
    @Override
    public void executeCommand(MessageDTO messageDTO) {
        if (messageDTO.getUserText().startsWith(BotCommands.CHANGED_USERS.getCommand())) {
            sendChanged(messageDTO.getChatId(), messageDTO.getUserId().toString());
        }
    }
}
