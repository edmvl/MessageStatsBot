package com.pathz.tgbot.messageStatBot.service;

import com.pathz.tgbot.messageStatBot.message_executor.MessageExecutor;
import com.pathz.tgbot.messageStatBot.repo.StatsRepo;
import com.pathz.tgbot.messageStatBot.util.enums.ChatSettingConstants;
import com.pathz.tgbot.messageStatBot.util.MessageFormatter;
import com.rometools.rome.feed.synd.SyndCategory;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import lombok.SneakyThrows;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.data.convert.Jsr310Converters;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class HolidayService {

    private final MessageExecutor messageExecutor;

    private final StatsRepo statsRepo;
    private final SettingsService settingsService;

    public HolidayService(MessageExecutor messageExecutor, StatsRepo statsRepo, SettingsService settingsService) {
        this.messageExecutor = messageExecutor;
        this.statsRepo = statsRepo;
        this.settingsService = settingsService;
    }

    public List<String> findAllChats() {
        return statsRepo.findDistinctChatId();
    }

    public void sendHolidaysAllChat() {
        String holidays = getHolidays();
        List<String> chatIds = findAllChats();
        chatIds.forEach(chatId -> {
            try {
                if (settingsService.isEnabled(chatId, ChatSettingConstants.ENABLE_HOLIDAYS)) {
                    sendMessage(chatId, holidays);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void sendHolidays(Long chatId) {
        String holidays = getHolidays();
        try {
            sendMessage(chatId.toString(), holidays);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendHolidays(Long chatId, Integer messageId) {
        sendHolidays(chatId);
        messageExecutor.deleteMessage(chatId, messageId);
    }

    @SneakyThrows
    public String getHolidays() {
        String url = "https://www.calend.ru/img/export/today-holidays.rss";
        SyndFeed feed = new SyndFeedInput().build(new XmlReader(new URL(url)));
        List<SyndEntry> entries = feed.getEntries();
        return entries.stream()
                .map(SyndEntry::getTitle)
                .collect(Collectors.joining("\n"));
    }

    private void sendMessage(String chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        messageExecutor.sendMessage(sendMessage);
    }

}
