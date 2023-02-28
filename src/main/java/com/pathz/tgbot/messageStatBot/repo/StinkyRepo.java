package com.pathz.tgbot.messageStatBot.repo;

import com.pathz.tgbot.messageStatBot.entity.Stinky;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface StinkyRepo extends JpaRepository<Stinky, Long> {

    Stinky findByChatIdAndDate(String chatId, LocalDate date);

}
