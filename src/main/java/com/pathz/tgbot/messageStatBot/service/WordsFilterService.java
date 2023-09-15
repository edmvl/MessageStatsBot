package com.pathz.tgbot.messageStatBot.service;

import com.pathz.tgbot.messageStatBot.dto.MessageDTO;
import com.pathz.tgbot.messageStatBot.entity.Settings;
import com.pathz.tgbot.messageStatBot.entity.WordsFilter;
import com.pathz.tgbot.messageStatBot.message_executor.MessageExecutor;
import com.pathz.tgbot.messageStatBot.repo.SettingsRepo;
import com.pathz.tgbot.messageStatBot.repo.WordsFilterRepo;
import com.pathz.tgbot.messageStatBot.util.MessageFormatter;
import com.pathz.tgbot.messageStatBot.util.enums.BotCommands;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class WordsFilterService implements CommandExecutable {
    private final MessageExecutor messageExecutor;

    private final WordsFilterRepo wordsFilterRepo;

    private final SettingsRepo settingsRepo;

    public WordsFilterService(MessageExecutor messageExecutor, WordsFilterRepo wordsFilterRepo, SettingsRepo settingsRepo) {
        this.messageExecutor = messageExecutor;
        this.wordsFilterRepo = wordsFilterRepo;
        this.settingsRepo = settingsRepo;
    }

    public void deleteByFilter(MessageDTO messageDTO) {
        if (Objects.isNull(messageDTO.getUserText())) {
            return;
        }
        List<String> words = Arrays.stream(messageDTO.getUserText().split(" ")).toList();
        List<String> wordsToFilter = wordsFilterRepo.findAll().stream().map(WordsFilter::getWord).map(String::toLowerCase).toList();
        if (wordsToFilter.stream().anyMatch(s -> words.stream().anyMatch(s.toLowerCase()::equals))) {
            messageExecutor.deleteMessage(messageDTO.getChatId(), messageDTO.getMessageId());
        }
    }

    public void addWord(String word, Long userId, Long chatId, Integer messageId) {
        messageExecutor.deleteMessage(chatId, messageId);
        Settings settings = settingsRepo.findByChatIdAndUserId(chatId.toString(), userId.toString());
        if (Objects.isNull(settings) || !Boolean.TRUE.equals(settings.getIsAdmin())) {
            WordsFilter wordsFilter = new WordsFilter();
            wordsFilter.setWord(word);
            wordsFilterRepo.save(wordsFilter);
        }
    }
    @Override
    public void executeCommand(MessageDTO messageDTO) {
        if (messageDTO.getUserText().startsWith(BotCommands.ADD_WORD.getCommand())) {
            String[] s = messageDTO.getUserText().split(" ");
            if (s.length >= 2) {
                addWord(s[1], messageDTO.getUserId(), messageDTO.getChatId(), messageDTO.getMessageId());
            }
        }

    }

}
