package com.pathz.tgbot.messageStatBot.repo;

import com.pathz.tgbot.messageStatBot.entity.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ChallengeRepo extends JpaRepository<Challenge, Long> {
    Challenge findByChatIdAndDateTimeEndAfterAndFinished(String chatId, LocalDateTime dateTime, Boolean finished);
    Challenge findByChatIdAndDateTimeEndLessThanAndFinished(String chatId, LocalDateTime dateTime, Boolean finished);
}
