package com.pathz.tgbot.messageStatBot.service;

import com.pathz.tgbot.messageStatBot.dto.StatsDto;
import com.pathz.tgbot.messageStatBot.entity.Stats;
import com.pathz.tgbot.messageStatBot.util.mapper.StatsDtoMapper;
import com.pathz.tgbot.messageStatBot.repo.StatsRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.pathz.tgbot.messageStatBot.util.BotCommands.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class StatsService {

    private final StatsRepo statsRepo;
    private final StatsDtoMapper statsDtoMapper;

    public StatsService(StatsRepo statsRepo, StatsDtoMapper statsDtoMapper) {
        this.statsRepo = statsRepo;
        this.statsDtoMapper = statsDtoMapper;
    }

    public List<Stats> findAll() {
        return statsRepo.findAll();
    }

    public List<Stats> findTop25() {
        return statsRepo.findTop25ByOrderByCountDesc();
    }

    public boolean isExistByMessage(String message) {
        return statsRepo.existsByMessage(message);
    }

    public void processStatistic(String text) {
        processCountMessage(text);
        processUserMessageStats(text);
    }

    public String getStatistic() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(" === STATISTICS ===\n");

        List<StatsDto> statsDtos = findTop25()
                .stream()
                .map(statsDtoMapper::mapToDto)
                .collect(toList());

        for (StatsDto elem: statsDtos) {
            stringBuilder.append(elem.getMessage()).append(": ").append(elem.getCount()).append("\n");
        }

        return stringBuilder.toString();
    }

    public String getMostFrequencyWord() {
        return statsRepo.findTopByOrderByCountDesc().getMessage();
    }

    public String getAuthors() {
        String authors = "Bot was created by @akira_7 and @Yaarslaav";
        return authors;
    }

    public String getHelp() {
        String helpMessage = """
                %s - get information about the number of individual messages
                %s - get the most used word
                %s - creators
                """.formatted(STATS_COMMAND, GET_MOST_FREQ_WORD_COMMAND, GET_AUTHORS_COMMAND);
        return helpMessage;
    }


    private void processCountMessage(String text) {
        List<String> splitText = Arrays.stream(text.split(" ")).collect(Collectors.toList());
        splitText = splitText.stream().filter(x->x.length()>=3).collect(Collectors.toList());;

        for (String string : splitText) {
            String lowerWord = string.toLowerCase();

            if (isExistByMessage(lowerWord)) {
                Stats found = statsRepo.findByMessage(lowerWord);
                found.setCount(found.getCount() + 1);
                statsRepo.save(found);
            } else {
                StatsDto statsDto = new StatsDto(lowerWord, 1);
                Stats stats = statsDtoMapper.mapToEntity(statsDto);
                statsRepo.save(stats);
            }
        }
    }

    private void processUserMessageStats(String text) {

    }

    @Transactional
    public void deleteMessage(String splitElem) {
        statsRepo.deleteByMessage(splitElem);
    }
}
