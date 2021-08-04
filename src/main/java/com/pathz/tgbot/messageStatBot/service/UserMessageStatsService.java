package com.pathz.tgbot.messageStatBot.service;

import com.pathz.tgbot.messageStatBot.repo.UserMessageStatsRepo;
import org.springframework.stereotype.Service;

@Service
public class UserMessageStatsService {

    private final UserMessageStatsRepo userMessageStatsRepo;

    public UserMessageStatsService(UserMessageStatsRepo userMessageStatsRepo) {
        this.userMessageStatsRepo = userMessageStatsRepo;
    }

    
}
