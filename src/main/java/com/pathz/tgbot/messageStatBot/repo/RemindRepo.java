package com.pathz.tgbot.messageStatBot.repo;

import com.pathz.tgbot.messageStatBot.entity.Remind;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RemindRepo extends JpaRepository<Remind, Long> {
    List<Remind> findAllByActiveIsTrueAndChatId(String chatId);
}
