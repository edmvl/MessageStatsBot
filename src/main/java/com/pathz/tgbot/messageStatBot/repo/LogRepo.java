package com.pathz.tgbot.messageStatBot.repo;

import com.pathz.tgbot.messageStatBot.entity.Log;
import com.pathz.tgbot.messageStatBot.entity.Settings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogRepo extends JpaRepository<Log, Long> {
    Settings findByChatIdAndUserId(String chatId, String userId);

    @Query(value = "select string_agg(l2.user_name, ', ')  from ( select  l.user_id, user_name from log l " +
            "where chat_id=?1 group by l.user_id, l.user_name order by l.user_id desc ) l2  " +
            "group by l2.user_id having count(l2.user_name)>1", nativeQuery = true)
    List<String> findUserChangedHistoryByChatId(String chatId);
}
