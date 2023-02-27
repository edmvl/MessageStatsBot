package com.pathz.tgbot.messageStatBot.service;

import com.pathz.tgbot.messageStatBot.entity.Log;
import com.pathz.tgbot.messageStatBot.repo.LogRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LogService {

    private final LogRepo logRepo;

    public LogService(LogRepo logRepo) {
        this.logRepo = logRepo;
    }

    public void save(String chatId, String chatName, String userId, String userName, LocalDateTime dateTime, String text) {
        Log log = new Log();
        log.setChatId(chatId);
        log.setChatName(chatName);
        log.setUserId(userId);
        log.setUserName(userName);
        log.setDateTime(dateTime);
        log.setText(text);
        logRepo.save(log);
    }

}
