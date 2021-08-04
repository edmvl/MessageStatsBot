package com.pathz.tgbot.messageStatBot.repo;

import com.pathz.tgbot.messageStatBot.entity.UserMessageStats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserMessageStatsRepo extends JpaRepository<UserMessageStats, Long> {

}
