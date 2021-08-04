package com.pathz.tgbot.messageStatBot.util.mapper;

import com.pathz.tgbot.messageStatBot.dto.StatsDto;
import com.pathz.tgbot.messageStatBot.dto.UserMessageStatsDto;
import com.pathz.tgbot.messageStatBot.entity.Stats;
import com.pathz.tgbot.messageStatBot.entity.UserMessageStats;
import org.springframework.stereotype.Service;

@Service
public class UserMessageDtoMapper {

    public UserMessageStatsDto mapToDto(UserMessageStats userMessageStats) {
        return new UserMessageStatsDto(userMessageStats.getUsername(), userMessageStats.getCount());
    }

    public UserMessageStats mapToEntity(UserMessageStatsDto userMessageStatsDto) {
        UserMessageStats userMessageStats = new UserMessageStats();

        userMessageStats.setUsername(userMessageStatsDto.getUsername());
        userMessageStats.setCount(userMessageStatsDto.getCount());

        return userMessageStats;
    }
}