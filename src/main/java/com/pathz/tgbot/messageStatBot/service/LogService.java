package com.pathz.tgbot.messageStatBot.service;

import com.pathz.tgbot.messageStatBot.entity.Log;
import com.pathz.tgbot.messageStatBot.repo.LogRepo;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.logging.Level;

@Service
@lombok.extern.java.Log
public class LogService {

    private final LogRepo logRepo;

    public LogService(LogRepo logRepo) {
        this.logRepo = logRepo;
    }

    public void save(String chatId, String chatName, String userId, String userName, LocalDateTime dateTime, String text) {
        log.log(Level.ALL, chatName + " " + userName + " " + dateTime.getHour() + ":" +
                dateTime.getMinute()+ ":" + dateTime.getSecond() + " " + text
        );
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
