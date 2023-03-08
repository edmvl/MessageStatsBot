package com.pathz.tgbot.messageStatBot.repo;

import com.pathz.tgbot.messageStatBot.dto.ChattyDaysDto;
import com.pathz.tgbot.messageStatBot.dto.StatsViewDto;
import com.pathz.tgbot.messageStatBot.entity.Stats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StatsRepo extends JpaRepository<Stats, Long> {

    Stats findByUserIdAndChatIdAndDate(String userId, String chatId, LocalDate date);

    boolean existsByUserIdAndChatIdAndAndDate(String chatId, String userId, LocalDate date);;

    List<Stats> findFirst10ByChatIdAndDateOrderByCountDesc(String chatId, LocalDate date);

    List<Stats> findByChatIdAndDateOrderByCountDesc(String chatId, LocalDate date);

    @Query(value = "select sum(s.count) as count, s.user_id as userId, s.chat_id as chatId from stats s" +
            " where s.chat_id=?1 and s.date between ?2 and ?3 group by user_id, chat_id order by count desc", nativeQuery = true)
    List<StatsViewDto> findByChatIdAndDateBetweenOrderByCountDesc(String chatId, LocalDate startDate, LocalDate endDate);

    @Query("select distinct s.userId from Stats s where s.chatId=?1")
    List<String> findDistinctUserIdByChatId(String chatId);

    @Query("select distinct s.chatId from Stats s")
    List<String> findDistinctChatId();

    @Query(value = "select sum(s.count) as sm, s.date from stats s  where s.chat_id=?1 group by date order by sm desc limit 10", nativeQuery = true)
    List<ChattyDaysDto> findTopChattyDays(String chatId);
}
