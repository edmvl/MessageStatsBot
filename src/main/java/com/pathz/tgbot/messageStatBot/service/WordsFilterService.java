package com.pathz.tgbot.messageStatBot.service;

import com.pathz.tgbot.messageStatBot.entity.WordsFilter;
import com.pathz.tgbot.messageStatBot.message_executor.MessageExecutor;
import com.pathz.tgbot.messageStatBot.repo.WordsFilterRepo;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class WordsFilterService {
    private final MessageExecutor messageExecutor;

    private final WordsFilterRepo wordsFilterRepo;

    public WordsFilterService(MessageExecutor messageExecutor, WordsFilterRepo wordsFilterRepo) {
        this.messageExecutor = messageExecutor;
        this.wordsFilterRepo = wordsFilterRepo;
    }

    public void deleteByFilter(Long chatId, Integer messageId, String text) {
        List<String> words = Arrays.stream(text.split(" ")).toList();
        List<String> wordsToFilter = wordsFilterRepo.findAll().stream().map(WordsFilter::getWord).toList();
        if (wordsToFilter.stream().anyMatch(s -> words.stream().anyMatch(s.toLowerCase()::equals))) {
            messageExecutor.deleteMessage(chatId, messageId);
        }
    }
}
