package com.pathz.tgbot.messageStatBot.service;

import com.pathz.tgbot.messageStatBot.dto.StatsDto;
import com.pathz.tgbot.messageStatBot.entity.Stats;
import com.pathz.tgbot.messageStatBot.message_executor.MessageExecutor;
import com.pathz.tgbot.messageStatBot.repo.StatsRepo;
import com.pathz.tgbot.messageStatBot.util.mapper.StatsDtoMapper;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.pathz.tgbot.messageStatBot.util.BotCommands.values;

@Service
public class StatsService {

    private final StatsRepo statsRepo;
    private final StatsDtoMapper statsDtoMapper;
    private final MessageExecutor messageExecutor;

    public StatsService(StatsRepo statsRepo, StatsDtoMapper statsDtoMapper, MessageExecutor messageExecutor) {
        this.statsRepo = statsRepo;
        this.statsDtoMapper = statsDtoMapper;
        this.messageExecutor = messageExecutor;
    }

    public boolean isExistByMessage(String chatId, String userId, LocalDate date) {
        return statsRepo.existsByUserIdAndChatIdAndAndDate(chatId, userId, date);
    }

    public void processStatistic(String chatId, String userId, String userName, String name) {
        processCounting(chatId, userId, userName, name);
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
                StatsDto statsDto = new StatsDto(
                        message.getChatId().toString(),
                        user.getId().toString(),
                        LocalDate.now(),
                        0,
                        user.getUserName(),
                        user.getFirstName() + " " + user.getLastName()
                );
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

    public List<Stats> getTop10ChattyUserId(Long chatId) {
        return statsRepo.findFirst10ByChatIdAndDateOrderByCountDesc(chatId.toString(), LocalDate.now());
    }

    public List<Stats> getTopChattyUserId(Long chatId) {
        return statsRepo.findByChatIdAndDateOrderByCountDesc(chatId.toString(), LocalDate.now());
    }

    public List<Stats> getTop10ChattyUserId(String chatId) {
        return statsRepo.findFirst10ByChatIdAndDateOrderByCountDesc(chatId, LocalDate.now());
    }

    private void processCounting(String chatId, String userId, String userName, String name) {
        if (isExistByMessage(userId, chatId, LocalDate.now())) {
            Stats found = statsRepo.findByUserIdAndChatIdAndDate(userId, chatId, LocalDate.now());
            found.setCount(found.getCount() + 1);
            statsRepo.save(found);
        } else {
            StatsDto statsDto = new StatsDto(
                    chatId,
                    userId,
                    LocalDate.now(),
                    1,
                    userName,
                    name
            );
            Stats stats = statsDtoMapper.mapToEntity(statsDto);
            statsRepo.save(stats);
        }
    }

    public List<String> findAllChats() {
        return statsRepo.findDistinctChatId();
    }

    public void sendStats(Long chatId){
        List<Stats> top = getTopChattyUserId(chatId);
        String caption = "Паянхи статистика:\n";
        sendMessage(chatId, top, caption);
    }

    private void sendMessage(Long chatId, List<Stats> top, String caption) {
        SendMessage sendMessage = new SendMessage();
        StringBuilder text = new StringBuilder();
        text.append(caption);
        ArrayList<MessageEntity> messageEntities = new ArrayList<>();
        top.forEach(stats -> {
            User user = null;
            while (Objects.isNull(user)){
                user = messageExecutor.searchUsersInChat(chatId.toString(), stats.getUserId()).getUser();
            }
            String firstName = user.getFirstName();
            String lastName = user.getLastName();
            addMessageEntity(text, messageEntities, stats, user, firstName, lastName);
        });
        if (messageEntities.isEmpty()) {
            sendMessage.setText("Тем çирман паян...");
        } else {
            sendMessage.setEntities(messageEntities);
            sendMessage.setText(text.toString());
        }
        sendMessage.setChatId(chatId);
        messageExecutor.sendMessage(sendMessage);
    }

    public static void addMessageEntity(
            StringBuilder text, ArrayList<MessageEntity> messageEntities, Stats stats, User user, String firstName, String lastName
    ) {
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setUser(user);
        messageEntity.setOffset(text.length());
        String userIdentityText = firstName + " " + (Objects.nonNull(lastName) ? lastName : "") + "(" + stats.getCount() + ")" + "\n";
        text.append(userIdentityText);
        messageEntity.setLength(userIdentityText.length());
        messageEntity.setType("text_mention");
        messageEntities.add(messageEntity);
    }

    public void sendChatty(Long chatId) {
        List<Stats> top = getTop10ChattyUserId(chatId);
        String caption = "Сурăх тути çиекеннисем:\n";
        sendMessage(chatId, top, caption);
    }

    public void sendChattyDays(Long chatId) {

    }

    public void sendStats(Long chatId, Integer messageId) {
        sendStats(chatId);
        messageExecutor.deleteMessage(chatId, messageId);
    }

    public void sendChatty(Long chatId, Integer messageId) {
        sendChatty(chatId);
        messageExecutor.deleteMessage(chatId, messageId);
    }

    public void sendChattyDays(Long chatId, Integer messageId) {
        sendChattyDays(chatId);
        messageExecutor.deleteMessage(chatId, messageId);
    }
}
