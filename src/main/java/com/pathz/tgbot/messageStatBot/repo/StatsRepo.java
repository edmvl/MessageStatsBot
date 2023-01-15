package com.pathz.tgbot.messageStatBot.repo;

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

    @Query("select distinct s.userId from Stats s where s.chatId=?1")
    List<String> findDistinctUserIdByChatId(String chatId);

    @Query("select distinct s.chatId from Stats s")
    List<String> findDistinctChatId();

/*    @Query("select sum(s.count) as sum, s.date from Stats s  where s.chatId=?1 group by date order by s.sum desc limit 10")
    List<String> findTopChattyDays();*/
}
