package com.pathz.tgbot.messageStatBot.handler;

import com.pathz.tgbot.messageStatBot.dto.MessageDTO;
import com.pathz.tgbot.messageStatBot.message_executor.MessageExecutor;
import com.pathz.tgbot.messageStatBot.service.*;
import com.pathz.tgbot.messageStatBot.util.enums.BotCommands;
import com.pathz.tgbot.messageStatBot.util.MessageFormatter;
import com.pathz.tgbot.messageStatBot.util.enums.HoroscopeEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.stickers.Sticker;

import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Stream;

@Component
public class MessageHandler implements Handler<Message> {

    private final MessageExecutor messageExecutor;
    private final StatsService statsService;
    private final LogService logService;
    private final WordsFilterService wordsFilterService;

    private final Logger logger = Logger.getLogger("MessageHandler");

    private final List<CommandExecutable> commandExecutables;
    @Value("${telegram.bot.username}")
    private String botUsername;

    @Lazy
    public MessageHandler(MessageExecutor messageExecutor, StatsService service, LogService logService,
                          WordsFilterService wordsFilterService, List<CommandExecutable> commandExecutables)
    {
        this.messageExecutor = messageExecutor;
        this.statsService = service;
        this.logService = logService;
        this.wordsFilterService = wordsFilterService;
        this.commandExecutables = commandExecutables;
    }

    @Override
    public void choose(Message message) {
        MessageDTO messageDTO = MessageDTO.builder()
                .chatId(message.getChatId())
                .chatName(message.getChat().getTitle())
                .messageId(message.getMessageId())
                .userId(message.getFrom().getId())
                .userName(message.getFrom().getUserName())
                .userText(message.hasText() ? String.join("", message.getText().split("@" + botUsername)) : "")
                .from(
                        MessageFormatter.trimNull(
                                message.getFrom().getFirstName(),
                                message.getFrom().getLastName(),
                                "(" + message.getFrom().getUserName() + ")"
                        )
                )
                .build();
        wordsFilterService.deleteByFilter(messageDTO);
        logService.save(
                messageDTO.getChatId().toString(), message.getChat().getTitle(), messageDTO.getUserId().toString(), messageDTO.getFrom(), LocalDateTime.now(),
                messageDTO.getUserText(), getMaxSizePhoto(message), getDocumentId(message), getSticker(message)
        );
        if (messageDTO.getUserText().startsWith(BotCommands.HELP_COMMAND.getCommand())) {
            messageExecutor.sendMessage(messageDTO.getChatId(), statsService.getHelp());
        }

        if (getAllBotCommands().stream().anyMatch(s -> messageDTO.getUserText().toLowerCase(Locale.ROOT).startsWith(s.toLowerCase()))) {
            commandExecutables.forEach(service -> service.executeCommand(messageDTO));
        }
        statsService.processNewChatMembers(message);
        statsService.processLeftChatMembers(message);
    }

    private List<String> getAllBotCommands() {
        List<String> s1 = Arrays.stream(BotCommands.values()).map(BotCommands::getCommand).toList();
        List<String> s2 = Arrays.stream(HoroscopeEnum.values()).map(HoroscopeEnum::getName).toList();
        return Stream.concat(s1.stream(), s2.stream()).toList();
    }

    private String getSticker(Message message) {
        Sticker sticker = message.getSticker();
        return Objects.nonNull(sticker) ? sticker.getFileId() : null;
    }

    private String getDocumentId(Message message) {
        Document document = message.getDocument();
        if (Objects.isNull(document)) {
            return null;
        }
        return document.getFileId();
    }

    private String getMaxSizePhoto(Message message) {
        List<PhotoSize> photo = message.getPhoto();
        if (Objects.isNull(photo)) {
            return null;
        }
        Optional<PhotoSize> max = photo.stream().max(Comparator.comparingInt(PhotoSize::getFileSize));
        return max.map(PhotoSize::getFileId).orElse(null);
    }

}
