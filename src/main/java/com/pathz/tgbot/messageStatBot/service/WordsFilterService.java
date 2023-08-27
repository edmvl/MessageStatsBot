package com.pathz.tgbot.messageStatBot.service;

import com.pathz.tgbot.messageStatBot.entity.Settings;
import com.pathz.tgbot.messageStatBot.entity.WordsFilter;
import com.pathz.tgbot.messageStatBot.message_executor.MessageExecutor;
import com.pathz.tgbot.messageStatBot.repo.SettingsRepo;
import com.pathz.tgbot.messageStatBot.repo.WordsFilterRepo;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class WordsFilterService {
    private final MessageExecutor messageExecutor;

    private final WordsFilterRepo wordsFilterRepo;

    private final SettingsRepo settingsRepo;

    public WordsFilterService(MessageExecutor messageExecutor, WordsFilterRepo wordsFilterRepo, SettingsRepo settingsRepo) {
        this.messageExecutor = messageExecutor;
        this.wordsFilterRepo = wordsFilterRepo;
        this.settingsRepo = settingsRepo;
    }

    public void deleteByFilter(Long chatId, Integer messageId, String text) {
        if (Objects.isNull(text)) {
            return;
        }
        List<String> words = Arrays.stream(text.split(" ")).toList();
        List<String> wordsToFilter = wordsFilterRepo.findAll().stream().map(WordsFilter::getWord).map(String::toLowerCase).toList();
        if (wordsToFilter.stream().anyMatch(s -> words.stream().anyMatch(s.toLowerCase()::equals))) {
            messageExecutor.deleteMessage(chatId, messageId);
        }
    }

    public void addWord(String word, Long userId, Long chatId) {
        Settings settings = settingsRepo.findByChatIdAndUserId(chatId.toString(), userId.toString());
        if (Objects.isNull(settings) || !Boolean.TRUE.equals(settings.getIsAdmin())) {
            WordsFilter wordsFilter = new WordsFilter();
            wordsFilter.setWord(word);
            wordsFilterRepo.save(wordsFilter);
        }
    }
}
