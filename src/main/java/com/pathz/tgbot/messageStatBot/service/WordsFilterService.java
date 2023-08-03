package com.pathz.tgbot.messageStatBot.service;

import com.pathz.tgbot.messageStatBot.message_executor.MessageExecutor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class WordsFilterService {
    private final MessageExecutor messageExecutor;

    public WordsFilterService(MessageExecutor messageExecutor) {
        this.messageExecutor = messageExecutor;
    }

    public void deleteByFilter(Long chatId, Integer messageId, String text) {
        List<String> words = Arrays.stream(text.split(" ")).toList();
        List<String> wordsToFilter = List.of("бля", "блять", "сука", "гандон", "хуй", "хуя", "хуев", "хуёв");
        if (wordsToFilter.stream().anyMatch(s -> words.stream().anyMatch(s.toLowerCase()::equals))) {
            messageExecutor.deleteMessage(chatId, messageId);
        }
    }
}
