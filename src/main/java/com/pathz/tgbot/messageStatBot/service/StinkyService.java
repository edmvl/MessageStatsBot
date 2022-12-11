package com.pathz.tgbot.messageStatBot.service;

import com.pathz.tgbot.messageStatBot.entity.Stinky;
import com.pathz.tgbot.messageStatBot.repo.StinkyRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class StinkyService {

    private final StinkyRepo stinkyRepo;

    public StinkyService(StinkyRepo stinkyRepo) {
        this.stinkyRepo = stinkyRepo;
    }

    public Stinky findByMessage(String chatId, LocalDate date) {
        return stinkyRepo.findByChatIdAndDate(chatId, date);
    }
    public void save(String chatId, String userId, LocalDate date){
        Stinky stinky = new Stinky();
        stinky.setChatId(chatId);
        stinky.setUserId(userId);
        stinky.setDate(date);
        stinkyRepo.save(stinky);
    }
}
