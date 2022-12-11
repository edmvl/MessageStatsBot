package com.pathz.tgbot.messageStatBot.util.mapper;

import com.pathz.tgbot.messageStatBot.dto.StatsDto;
import com.pathz.tgbot.messageStatBot.entity.Stats;
import org.springframework.stereotype.Service;

@Service
public class StatsDtoMapper {

    public StatsDto mapToDto(Stats stats) {
        return new StatsDto(stats.getChatId(), stats.getUserId(), stats.getDate(), stats.getCount(), stats.getLogin());
    }

    public Stats mapToEntity(StatsDto statsDto) {
        Stats stats = new Stats();
        stats.setChatId(statsDto.getChatId());
        stats.setUserId(statsDto.getUserId());
        stats.setDate(statsDto.getDate());
        stats.setCount(statsDto.getCount());
        stats.setLogin(statsDto.getLogin());
        return stats;
    }
}
