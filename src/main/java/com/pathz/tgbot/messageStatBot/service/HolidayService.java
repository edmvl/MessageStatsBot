package com.pathz.tgbot.messageStatBot.service;

import com.pathz.tgbot.messageStatBot.message_executor.MessageExecutor;
import com.pathz.tgbot.messageStatBot.repo.StatsRepo;
import com.pathz.tgbot.messageStatBot.util.MessageFormatter;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.LocalDate;
import java.util.List;

@Service
public class HolidayService {

    private final MessageExecutor messageExecutor;

    private final StatsRepo statsRepo;

    public HolidayService(MessageExecutor messageExecutor, StatsRepo statsRepo) {
        this.messageExecutor = messageExecutor;
        this.statsRepo = statsRepo;
    }

    public List<String> findAllChats() {
        return statsRepo.findDistinctChatId();
    }

    public void sendHolidaysAllChat() {
        String holidays = getHolidays();
        List<String> chatIds = findAllChats();
        chatIds.forEach(chatId -> {
            sendMessage(chatId, holidays);
        });
    }


    public String getHolidays() {
        LocalDate now = LocalDate.now();
        String url = MessageFormatter.getUrlByDate(now);
        Document document = MessageFormatter.getHTMLPage(url);
        List<String> strings = document.body().select("span[itemprop='text']").eachText();
        return "Праздники сегодня:\n" + String.join("\n", strings);
    }

    private void sendMessage(String chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        messageExecutor.sendMessage(sendMessage);
    }

}
