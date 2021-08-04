package com.pathz.tgbot.messageStatBot.util.mapper;

import com.pathz.tgbot.messageStatBot.dto.StatsDto;
import com.pathz.tgbot.messageStatBot.entity.Stats;
import org.springframework.stereotype.Service;

@Service
public class StatsDtoMapper {

    public StatsDto mapToDto(Stats stats) {
        return new StatsDto(stats.getMessage(), stats.getCount());
    }

    public Stats mapToEntity(StatsDto statsDto) {
        Stats stats = new Stats();

        stats.setMessage(statsDto.getMessage());
        stats.setCount(statsDto.getCount());

        return stats;
    }
}
