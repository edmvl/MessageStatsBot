package com.pathz.tgbot.messageStatBot.service;

import com.pathz.tgbot.messageStatBot.message_executor.MessageExecutor;
import com.pathz.tgbot.messageStatBot.repo.StatsRepo;
import com.pathz.tgbot.messageStatBot.util.ChatSettingConstants;
import com.pathz.tgbot.messageStatBot.util.MessageFormatter;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

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
                    return;
                }
                sendMessage(chatId, holidays);
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

    public String getHolidays() {
        LocalDate now = LocalDate.now();
        String url = MessageFormatter.getUrlByDate(now);
        Document document = MessageFormatter.getHTMLPage(url);
        if (Objects.isNull(document)){
            return "";
        }
        Element body = document.body();
        List<String> strings = body.select("span[itemprop='text']").eachText();
        return "Праздники сегодня:\n" + String.join("\n", strings);
    }

    private void sendMessage(String chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        messageExecutor.sendMessage(sendMessage);
    }

}
