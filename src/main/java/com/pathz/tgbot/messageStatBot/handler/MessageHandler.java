package com.pathz.tgbot.messageStatBot.handler;

import com.pathz.tgbot.messageStatBot.dto.MessageDTO;
import com.pathz.tgbot.messageStatBot.message_executor.MessageExecutor;
import com.pathz.tgbot.messageStatBot.service.*;
import com.pathz.tgbot.messageStatBot.util.enums.BotCommands;
import com.pathz.tgbot.messageStatBot.util.MessageFormatter;
import com.pathz.tgbot.messageStatBot.util.enums.HoroscopeEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.games.Animation;
import org.telegram.telegrambots.meta.api.objects.stickers.Sticker;

import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Stream;

@Component
public class MessageHandler implements Handler<Message> {

    private final FileLoaderService fileLoaderService;
    private final LogService logService;
    private final WordsFilterService wordsFilterService;

    private final Logger logger = Logger.getLogger("MessageHandler");

    private final List<CommandExecutable> commandExecutables;
    private final List<AnswerCheckable> answerCheckables;
    @Value("${telegram.bot.username}")
    private String botUsername;

    @Lazy
    public MessageHandler(FileLoaderService fileLoaderService, LogService logService,
                          WordsFilterService wordsFilterService, List<CommandExecutable> commandExecutables,
                          List<AnswerCheckable> answerCheckables) {
        this.fileLoaderService = fileLoaderService;
        this.logService = logService;
        this.wordsFilterService = wordsFilterService;
        this.commandExecutables = commandExecutables;
        this.answerCheckables = answerCheckables;
    }

    @Override
    public void choose(Message message) {
        MessageDTO messageDTO = MessageDTO.builder()
                .chatId(message.getChatId())
                .chatName(message.getChat().getTitle())
                .messageId(message.getMessageId())
                .replyMessageId(Objects.nonNull(message.getReplyToMessage()) ? message.getReplyToMessage().getMessageId() : null)
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
        Pair<String, String> file = getFile(message);
        logService.save(
                messageDTO.getChatId().toString(), message.getChat().getTitle(), messageDTO.getUserId().toString(), messageDTO.getFrom(), LocalDateTime.now(),
                messageDTO.getUserText(), file
        );
        if (Objects.nonNull(file)) {
            String second = file.getSecond();
            fileLoaderService.downloadFile(second);
        }
        if (getAllBotCommands().stream().anyMatch(s -> messageDTO.getUserText().toLowerCase(Locale.ROOT).startsWith(s.toLowerCase()))) {
            commandExecutables.forEach(service -> service.executeCommand(messageDTO));
        }
        answerCheckables.forEach(service -> service.checkAnswer(messageDTO));
    }

    private List<String> getAllBotCommands() {
        List<String> s1 = Arrays.stream(BotCommands.values()).map(BotCommands::getCommand).toList();
        List<String> s2 = Arrays.stream(HoroscopeEnum.values()).map(HoroscopeEnum::getName).toList();
        return Stream.concat(s1.stream(), s2.stream()).toList();
    }

    private Pair<String, String> getFile(Message message) {
        List<PhotoSize> photo = message.getPhoto();
        if (Objects.nonNull(photo)) {
            Optional<PhotoSize> max = photo.stream().max(Comparator.comparingInt(PhotoSize::getFileSize));
            return Pair.of("photo", max.map(PhotoSize::getFileId).orElse(null));
        }
        Document document = message.getDocument();
        if (Objects.nonNull(document)) {
            return Pair.of("document", document.getFileId());
        }
        Sticker sticker = message.getSticker();
        if (Objects.nonNull(sticker)) {
            return Pair.of("sticker", sticker.getFileId());
        }
        VideoNote videoNote = message.getVideoNote();
        if (Objects.nonNull(videoNote)) {
            return Pair.of("video_note", videoNote.getFileId());
        }
        Video video = message.getVideo();
        if (Objects.nonNull(video)) {
            return Pair.of("video", video.getFileId());
        }
        Voice voice = message.getVoice();
        if (Objects.nonNull(voice)) {
            return Pair.of("voice", voice.getFileId());
        }
        Animation animation = message.getAnimation();
        if (Objects.nonNull(animation)) {
            return Pair.of("animation", animation.getFileId());
        }
        Audio audio = message.getAudio();
        if (Objects.nonNull(audio)) {
            return Pair.of("audio", audio.getFileId());
        }
        return null;
    }

}
