package com.pathz.tgbot.messageStatBot.repo;

import com.pathz.tgbot.messageStatBot.dto.StatsViewDto;
import com.pathz.tgbot.messageStatBot.entity.Log;
import com.pathz.tgbot.messageStatBot.entity.Settings;
import com.pathz.tgbot.messageStatBot.entity.Stats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LogRepo extends JpaRepository<Log, Long> {
    Settings findByChatIdAndUserId(String chatId, String userId);

    @Query(value = "select chat_id, user_id, count(0) from log " +
            "where chat_id=?1 and date_time between '?2 0:0:0.0' and '?2 23:59:59.9' " +
            "group by user_id, chat_id;", nativeQuery = true)
    List<StatsViewDto> findFirst10ByChatIdAndDateOrderByCountDesc(String chatId, LocalDate date);

}
