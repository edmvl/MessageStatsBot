package com.pathz.tgbot.messageStatBot.service;

import com.pathz.tgbot.messageStatBot.entity.Log;
import com.pathz.tgbot.messageStatBot.repo.LogRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Service
@lombok.extern.java.Log
public class LogService extends MessageSender {

    private final LogRepo logRepo;

    public LogService(LogRepo logRepo) {
        this.logRepo = logRepo;
    }

    public void save(
            String chatId, String chatName, String userId, String userName, LocalDateTime dateTime,
            String text, String photo, String documentId, String sticker
    ) {
        log.log(Level.INFO, chatName + " " + userName + " " + " " + text);
        Log log = new Log();
        log.setChatId(chatId);
        log.setChatName(chatName);
        log.setUserId(userId);
        log.setUserName(userName);
        log.setDateTime(dateTime);
        log.setText(text);
        log.setPhoto(photo);
        log.setDocument(documentId);
        log.setStiker(sticker);
        logRepo.save(log);
    }

    public void sendChanged(Long chatId) {
        List<String> userChangedHistoryByChatId = logRepo.findUserChangedHistoryByChatId(String.valueOf(chatId));
        String collect = userChangedHistoryByChatId.stream().map(s -> s + "\n").collect(Collectors.joining("===================\n"));
        sendMessage(chatId, collect);
    }
}
