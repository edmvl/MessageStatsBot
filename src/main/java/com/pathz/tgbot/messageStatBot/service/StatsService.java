package com.pathz.tgbot.messageStatBot.service;

import com.pathz.tgbot.messageStatBot.dto.ChattyDaysDto;
import com.pathz.tgbot.messageStatBot.dto.StatsDto;
import com.pathz.tgbot.messageStatBot.entity.Settings;
import com.pathz.tgbot.messageStatBot.entity.Stats;
import com.pathz.tgbot.messageStatBot.message_executor.MessageExecutor;
import com.pathz.tgbot.messageStatBot.repo.SettingsRepo;
import com.pathz.tgbot.messageStatBot.repo.StatsRepo;
import com.pathz.tgbot.messageStatBot.util.mapper.StatsDtoMapper;
import org.springframework.format.datetime.standard.TemporalAccessorParser;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.User;

import javax.swing.text.DateFormatter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.stream.Collectors;

import static com.pathz.tgbot.messageStatBot.util.BotCommands.values;

@Service
public class StatsService {

    private final StatsRepo statsRepo;
    private final SettingsRepo settingsRepo;
    private final StatsDtoMapper statsDtoMapper;
    private final MessageExecutor messageExecutor;

    public StatsService(StatsRepo statsRepo, SettingsRepo settingsRepo, StatsDtoMapper statsDtoMapper, MessageExecutor messageExecutor) {
        this.statsRepo = statsRepo;
        this.settingsRepo = settingsRepo;
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
            if (!statsRepo.existsByUserIdAndChatIdAndAndDate(user.getId().toString(), message.getChatId().toString(), LocalDate.now())) {
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

    public List<Stats> getTopChattyUserId(Long chatId, LocalDate date) {
        return statsRepo.findByChatIdAndDateOrderByCountDesc(chatId.toString(), date);
    }

    public List<Stats> getTop10ChattyUserId(String chatId) {
        return statsRepo.findFirst10ByChatIdAndDateOrderByCountDesc(chatId, LocalDate.now());
    }

    private void processCounting(String chatId, String userId, String userName, String name) {
        Settings byChatIdAndUserId = settingsRepo.findByChatIdAndUserId(chatId, userId);
        if (Objects.nonNull(byChatIdAndUserId) && byChatIdAndUserId.getSkipStats()) {
            return;
        }
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

    public void sendStats(Long chatId, LocalDate date) {
        List<Stats> top = getTopChattyUserId(chatId, date);
        String caption = "Статистика на " + date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + "\n";
        if (date.equals(LocalDate.now())) {
            caption = "Паянхи статистика:\n";
        }
        sendMessage(chatId, top, caption);
    }

    private void sendMessage(Long chatId, List<Stats> top, String caption) {
        SendMessage sendMessage = new SendMessage();
        StringBuilder text = new StringBuilder();
        text.append(caption);
        ArrayList<MessageEntity> messageEntities = new ArrayList<>();
        top.forEach(stats -> {
            User user = messageExecutor.searchUsersInChat(chatId.toString(), stats.getUserId());
            String firstName = Objects.nonNull(user) ? user.getFirstName() : "deleted";
            String lastName = Objects.nonNull(user) ? user.getLastName() : "deleted";
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

    private void sendReply(Long chatId, String text, Integer messId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.setReplyToMessageId(messId);
        messageExecutor.sendMessage(sendMessage);
    }

    private static void addMessageEntity(
            StringBuilder text, ArrayList<MessageEntity> messageEntities, Stats stats, User user, String firstName, String lastName
    ) {
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setOffset(text.length());
        String userIdentityText = firstName + " " + (Objects.nonNull(lastName) ? lastName : "") + "(" + stats.getCount() + ")" + "\n";
        text.append(userIdentityText);
        messageEntity.setLength(userIdentityText.length());
        if (Objects.nonNull(user)) {
            messageEntity.setUser(user);
            messageEntity.setType("text_mention");
        } else {
            messageEntity.setType("mention");
        }
        messageEntities.add(messageEntity);
    }

    public void sendChatty(Long chatId) {
        List<Stats> top = getTop10ChattyUserId(chatId);
        String caption = "Сурăх тути çиекеннисем:\n";
        sendMessage(chatId, top, caption);
    }

    public void sendChattyDays(Long chatId) {
        List<ChattyDaysDto> topChattyDays = statsRepo.findTopChattyDays(chatId.toString());
        String message = topChattyDays.stream()
                .map(dto -> dto.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " (" + dto.getSm() + " хут пакăлтатнă)").collect(Collectors.joining("\n"));
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Сурăх тути кунĕсем:\n" + message);
        messageExecutor.sendMessage(sendMessage);
    }

    public void sendStats(Long chatId, Integer messageId, LocalDate date) {
        sendStats(chatId, date);
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

    public void skipStats(Long chatId, Long userId, Integer messageId) {
        Settings settings = settingsRepo.findByChatIdAndUserId(chatId.toString(), userId.toString());
        if (Objects.isNull(settings) || !Boolean.TRUE.equals(settings.getIsAdmin())) {
            settings = new Settings();
            settings.setChatId(String.valueOf(chatId));
            settings.setUserId(String.valueOf(userId));
            settings.setSkipStats(false);
            settingsRepo.save(settings);
            sendReply(chatId, "Доступно только администратору бота", messageId);
            return;
        }
        settings.setSkipStats(true);
        settingsRepo.save(settings);
        sendReply(chatId, "Вы добавлены в спиок игнора статистики", messageId);
    }

    public void sendStats(Long chatId, Integer messageId, String date) {
        LocalDate dt = Objects.isNull(date) ? LocalDate.now() : LocalDate.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        sendStats(chatId, messageId, dt);
    }
}
