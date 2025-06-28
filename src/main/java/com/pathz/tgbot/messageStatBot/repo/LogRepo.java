package com.pathz.tgbot.messageStatBot.repo;

import com.pathz.tgbot.messageStatBot.dto.ChattyDaysDto;
import com.pathz.tgbot.messageStatBot.dto.StatsViewDto;
import com.pathz.tgbot.messageStatBot.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LogRepo extends JpaRepository<Log, Long> {

    @Query(value = "select user_name from log where user_id=?2 and chat_id = ?1 order by id desc limit 1;", nativeQuery = true)
    List<String> findLastUserNameByChatId(String chatId, String userId);

    @Query(value = "select string_agg(l2.user_name, ', ')  from ( select  l.user_id, user_name from log l " +
            "where chat_id=?1 and user_id=?2 group by l.user_id, l.user_name order by l.user_id desc ) l2  " +
            "group by l2.user_id", nativeQuery = true)
    List<String> findUserChangedHistoryByChatId(String chatId, String userId);

    @Query(value = "select distinct l.chat_id from log l where cast(l.chat_id as bigint)<0", nativeQuery = true)
    List<String> findDistinctChatId();

    @Query(value = "select TO_DATE(TO_CHAR(date_time, 'dd.mm.yyyy'), 'dd.mm.yyyy') as date, count(0) as sm from log l " +
            "where l.chat_id = ?1 group by date order by sm desc limit 10", nativeQuery = true)
    List<ChattyDaysDto> findTopChattyDays(String chatId);

    @Query(value = "select  chat_id as chatId, user_id as userId, count(0) as count from log l " +
            "where l.chat_id = ?1 and date_time between ?2 and ?3 " +
            "group by user_id, chat_id order by count desc", nativeQuery = true)
    List<StatsViewDto> findByChatIdAndDateBetweenOrderByCountDesc(String chatId, LocalDateTime startDate, LocalDateTime endDate);

    @Query(value = "select  chat_id as chatId, user_id as userId, count(0) as count from log l " +
            "where l.chat_id = ?1 and date_time between ?2 and ?3 " +
            "group by user_id, chat_id order by count desc  limit 10", nativeQuery = true)
    List<StatsViewDto> findFirst10ByChatIdAndDateOrderByCountDesc(String chatId, LocalDateTime startDate, LocalDateTime endDate);

    @Query(value = "select distinct l.user_id from log l where l.chat_id = ?1", nativeQuery = true)
    List<String> findDistinctUserIdByChatId(String chatId);

    List<Log> getLogByChatIdAndDateTimeBetween(String chatId, LocalDateTime dateTimeAfter, LocalDateTime dateTimeBefore);
}
