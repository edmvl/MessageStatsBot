package com.pathz.tgbot.messageStatBot.service;

import com.pathz.tgbot.messageStatBot.dto.ChattyDaysDto;
import com.pathz.tgbot.messageStatBot.dto.MessageDTO;
import com.pathz.tgbot.messageStatBot.dto.StatsViewDto;
import com.pathz.tgbot.messageStatBot.entity.Settings;
import com.pathz.tgbot.messageStatBot.message_executor.MessageExecutor;
import com.pathz.tgbot.messageStatBot.repo.LogRepo;
import com.pathz.tgbot.messageStatBot.repo.SettingsRepo;
import com.pathz.tgbot.messageStatBot.util.enums.BotCommands;
import com.pathz.tgbot.messageStatBot.util.enums.ChatSettingConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.pathz.tgbot.messageStatBot.util.enums.BotCommands.values;

@Service
public class StatsService implements CommandExecutable {

    @Value("${telegram.bot.username}")
    private String botUsername;

    private final LogRepo logRepo;
    private final SettingsRepo settingsRepo;
    private final MessageExecutor messageExecutor;

    private final SettingsService settingsService;

    public StatsService(
            LogRepo logRepo, SettingsRepo settingsRepo, MessageExecutor messageExecutor, SettingsService settingsService
    ) {
        this.logRepo = logRepo;
        this.settingsRepo = settingsRepo;
        this.messageExecutor = messageExecutor;
        this.settingsService = settingsService;
    }

    public String getHelp() {
        return Arrays.stream(values()).filter(c -> !c.isForAdmin()).map(
                botCommand -> botCommand.getCommand() + " : " + botCommand.getExplainer()).collect(Collectors.joining("\n")
        );
    }

    public List<StatsViewDto> getTop10ChattyUserId(Long chatId) {
        LocalDate startOfDay = LocalDate.now();
        LocalDateTime localDateTime = startOfDay.atStartOfDay();
        LocalDateTime endDate = startOfDay.atStartOfDay().withHour(23).withMinute(59).withSecond(59);
        return logRepo.findFirst10ByChatIdAndDateOrderByCountDesc(chatId.toString(), localDateTime, endDate);
    }

    public List<StatsViewDto> getTopChattyUserId(Long chatId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endDate = date.atStartOfDay().withHour(23).withMinute(59).withSecond(59);
        return logRepo.findByChatIdAndDateBetweenOrderByCountDesc(chatId.toString(), startOfDay, endDate);
    }

    public List<StatsViewDto> getTopChattyWeek(Long chatId) {
        LocalDateTime startDate = LocalDate.now().with(TemporalAdjusters.previous(DayOfWeek.MONDAY)).atStartOfDay();
        LocalDateTime endDate = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.SUNDAY))
                .atStartOfDay().withHour(23).withMinute(59).withSecond(59);
        return logRepo.findByChatIdAndDateBetweenOrderByCountDesc(chatId.toString(), startDate, endDate);
    }

    public List<String> findAllChats() {
        return logRepo.findDistinctChatId();
    }

    public void sendStatAllChat() {
        for (String chatId : findAllChats()) {
            if (settingsService.isDisabled(chatId, ChatSettingConstants.ENABLE_STATS)) {
                continue;
            }
            sendChatty(Long.valueOf(chatId));
        }

    }

    public void sendStats(Long chatId, LocalDate date) {
        List<StatsViewDto> top = getTopChattyUserId(chatId, date);
        StringBuilder text = new StringBuilder("Статистика на " + date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + "\n");
        int sum = top.stream().map(StatsViewDto::getCount).mapToInt(value -> value).sum();
        if (date.equals(LocalDate.now())) {
            text = new StringBuilder("Паянхи статистика:\nВсего:" + sum + "\n");
        }
        List<MessageEntity> messageEntities = getMessageEntities(top, chatId, text);
        sendMessage(chatId, messageEntities, text.toString());
    }

    public void sendWeekStats(Long chatId) {
        List<StatsViewDto> top = getTopChattyWeek(chatId);
        StringBuilder text = new StringBuilder();
        text.append("Статистика за эту неделю\n");
        List<MessageEntity> messageEntities = getMessageEntities2(top, chatId, text);
        sendMessage(chatId, messageEntities, text.toString());
    }

    private List<MessageEntity> getMessageEntities2(List<StatsViewDto> top, Long chatId, StringBuilder text) {
        ArrayList<MessageEntity> messageEntities = new ArrayList<>();
        top.forEach(stats -> {
            User user = messageExecutor.searchUsersInChat(chatId.toString(), stats.getUserId());
            String firstName = Objects.nonNull(user) ? user.getFirstName() : "deleted";
            String lastName = Objects.nonNull(user) ? user.getLastName() : "deleted";
            messageEntities.add(getMessageEntity(text, stats.getCount(), user, firstName, lastName));
        });
        return messageEntities;
    }

    private List<MessageEntity> getMessageEntities(List<StatsViewDto> top, Long chatId, StringBuilder text) {
        ArrayList<MessageEntity> messageEntities = new ArrayList<>();
        top.forEach(stats -> {
            User user = messageExecutor.searchUsersInChat(chatId.toString(), stats.getUserId());
            String firstName = Objects.nonNull(user) ? user.getFirstName() : "deleted";
            String lastName = Objects.nonNull(user) ? user.getLastName() : "deleted";
            messageEntities.add(getMessageEntity(text, stats.getCount(), user, firstName, lastName));
        });
        return messageEntities;
    }

    private void sendMessage(Long chatId, List<MessageEntity> messageEntities, String text) {
        SendMessage sendMessage = new SendMessage();
        if (messageEntities.isEmpty()) {
            sendMessage.setText("Тем çирман паян...");
        } else {
            sendMessage.setEntities(messageEntities);
            sendMessage.setText(text);
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

    private static MessageEntity getMessageEntity(
            StringBuilder text, Integer count, User user, String firstName, String lastName
    ) {
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setOffset(text.length());
        String userIdentityText = firstName + " " + (Objects.nonNull(lastName) ? lastName : "") + "(" + count + ")" + "\n";
        text.append(userIdentityText);
        messageEntity.setLength(userIdentityText.length());
        if (Objects.nonNull(user)) {
            messageEntity.setUser(user);
            messageEntity.setType("text_mention");
        } else {
            messageEntity.setType("mention");
        }
        return messageEntity;
    }

    public void sendChatty(Long chatId) {
        List<StatsViewDto> top = getTop10ChattyUserId(chatId);
        StringBuilder text = new StringBuilder();
        text.append("Сурăх тути çиекеннисем:\n");
        List<MessageEntity> messageEntities = getMessageEntities(top, chatId, text);
        sendMessage(chatId, messageEntities, text.toString());
    }

    public void sendChattyDays(Long chatId) {
        List<ChattyDaysDto> topChattyDays = logRepo.findTopChattyDays(chatId.toString());
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

    public void sendWeekStats(Long chatId, Integer messageId) {
        sendWeekStats(chatId);
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

    @Override
    public void executeCommand(MessageDTO messageDTO) {
        if (messageDTO.getUserText().startsWith(BotCommands.GET_STATS_ALL.getCommand())) {
            String[] s = messageDTO.getUserText().split(" ");
            sendStats(messageDTO.getChatId(), messageDTO.getMessageId(), s.length > 1 ? s[1] : null);
        }
        if (messageDTO.getUserText().startsWith(BotCommands.GET_WEEK_STATS.getCommand())) {
            sendWeekStats(messageDTO.getChatId(), messageDTO.getMessageId());
        }
        if (messageDTO.getUserText().startsWith(BotCommands.GET_CHATTY.getCommand())) {
            sendChatty(messageDTO.getChatId(), messageDTO.getMessageId());
        }
        if (messageDTO.getUserText().startsWith(BotCommands.GET_CHATTY_DAYS.getCommand())) {
            sendChattyDays(messageDTO.getChatId(), messageDTO.getMessageId());
        }
        if (messageDTO.getUserText().startsWith(BotCommands.SKIP_STATS.getCommand())) {
            skipStats(messageDTO.getChatId(), messageDTO.getUserId(), messageDTO.getMessageId());
        }
    }
}
