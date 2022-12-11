package com.pathz.tgbot.messageStatBot.service;

import com.pathz.tgbot.messageStatBot.dto.StatsDto;
import com.pathz.tgbot.messageStatBot.entity.Stats;
import com.pathz.tgbot.messageStatBot.repo.StatsRepo;
import com.pathz.tgbot.messageStatBot.util.mapper.StatsDtoMapper;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.pathz.tgbot.messageStatBot.util.BotCommands.values;

@Service
public class StatsService {

    private final StatsRepo statsRepo;
    private final StatsDtoMapper statsDtoMapper;

    public StatsService(StatsRepo statsRepo, StatsDtoMapper statsDtoMapper) {
        this.statsRepo = statsRepo;
        this.statsDtoMapper = statsDtoMapper;
    }

    public boolean isExistByMessage(String chatId, String userId, LocalDate date) {
        return statsRepo.existsByUserIdAndChatIdAndAndDate(chatId, userId, date);
    }

    public void processStatistic(String chatId, String userId, String userName) {
        processCounting(chatId, userId, userName);
    }

    public String getHelp() {
        return Arrays.stream(values()).map(
                botCommand -> botCommand.getCommand() + " : " + botCommand.getExplainer()).collect(Collectors.joining("\n")
        );
    }

    public void processNewChatMembers(Message message) {
        List<User> newChatMembers = message.getNewChatMembers();
        if (Objects.isNull(newChatMembers) || newChatMembers.size() == 0) {
            return;
        }
        newChatMembers.forEach(user -> {
            if (!statsRepo.existsByUserIdAndChatIdAndAndDate(message.getChatId().toString(), user.getId().toString(), LocalDate.now())) {
                StatsDto statsDto = new StatsDto(message.getChatId().toString(), user.getId().toString(), LocalDate.now(), 0, user.getUserName());
                Stats stats = statsDtoMapper.mapToEntity(statsDto);
                statsRepo.save(stats);
            }
        });
    }

    public void processLeftChatMembers(Message message) {
        User leftChatMember = message.getLeftChatMember();
        if (Objects.isNull(leftChatMember)) {
            return;
        }
        Stats byUserIdAndChatId = statsRepo.findByUserIdAndChatIdAndDate(
                leftChatMember.getId().toString(), message.getChatId().toString(), LocalDate.now()
        );
        statsRepo.delete(byUserIdAndChatId);
    }

    public String getStinky(Message message) {
        List<String> distinctUserIdByChatId = statsRepo.findDistinctUserIdByChatId(message.getChatId().toString());
        int i = (int) (Math.random() * distinctUserIdByChatId.size());
        return distinctUserIdByChatId.get(i);
    }

    public List<Stats> getTopChattyUserId(Message message) {
        return statsRepo.findFirst10ByChatIdAndDateOrderByCountDesc(message.getChatId().toString(), LocalDate.now());
    }

    private void processCounting(String chatId, String userId, String userName) {
        if (isExistByMessage(userId, chatId, LocalDate.now())) {
            Stats found = statsRepo.findByUserIdAndChatIdAndDate(userId, chatId, LocalDate.now());
            found.setCount(found.getCount() + 1);
            statsRepo.save(found);
        } else {
            StatsDto statsDto = new StatsDto(chatId, userId, LocalDate.now(), 1, userName);
            Stats stats = statsDtoMapper.mapToEntity(statsDto);
            statsRepo.save(stats);
        }
    }

}
