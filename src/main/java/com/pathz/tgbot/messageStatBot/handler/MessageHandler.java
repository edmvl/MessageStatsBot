package com.pathz.tgbot.messageStatBot.handler;

import com.pathz.tgbot.messageStatBot.dto.FileDto;
import com.pathz.tgbot.messageStatBot.dto.MessageDTO;
import com.pathz.tgbot.messageStatBot.service.AnswerCheckable;
import com.pathz.tgbot.messageStatBot.service.CommandExecutable;
import com.pathz.tgbot.messageStatBot.service.FileLoaderService;
import com.pathz.tgbot.messageStatBot.service.LogService;
import com.pathz.tgbot.messageStatBot.service.WordsFilterService;
import com.pathz.tgbot.messageStatBot.util.enums.BotCommands;
import com.pathz.tgbot.messageStatBot.util.MessageFormatter;
import com.pathz.tgbot.messageStatBot.util.enums.FileTypes;
import com.pathz.tgbot.messageStatBot.util.enums.HoroscopeEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Audio;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Video;
import org.telegram.telegrambots.meta.api.objects.VideoNote;
import org.telegram.telegrambots.meta.api.objects.Voice;
import org.telegram.telegrambots.meta.api.objects.games.Animation;
import org.telegram.telegrambots.meta.api.objects.stickers.Sticker;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
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
                .dateTime(LocalDateTime.ofInstant(Instant.ofEpochSecond(message.getDate()), ZoneId.systemDefault()))
                .messageId(message.getMessageId())
                .replyMessageId(Objects.nonNull(message.getReplyToMessage()) ? message.getReplyToMessage().getMessageId() : null)
                .userId(message.getFrom().getId())
                .userName(message.getFrom().getUserName())
                .userFirstName(message.getFrom().getFirstName())
                .userLastName(message.getFrom().getLastName())
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
        FileDto file = getFile(message);
        logService.save(messageDTO, file);
        if (Objects.nonNull(file)) {
            fileLoaderService.downloadFile(file);
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

    private FileDto getFile(Message message) {
        List<PhotoSize> photo = message.getPhoto();
        if (Objects.nonNull(photo)) {
            Optional<PhotoSize> max = photo.stream().max(Comparator.comparingInt(PhotoSize::getFileSize));
            return new FileDto(FileTypes.PHOTO, max.map(PhotoSize::getFileId).orElse(null), null);
        }
        Document document = message.getDocument();
        if (Objects.nonNull(document)) {
            return new FileDto(FileTypes.DOCUMENT, document.getFileId(), document.getFileName());
        }
        Sticker sticker = message.getSticker();
        if (Objects.nonNull(sticker)) {
            return new FileDto(FileTypes.STICKER, sticker.getFileId(), null);
        }
        VideoNote videoNote = message.getVideoNote();
        if (Objects.nonNull(videoNote)) {
            return new FileDto(FileTypes.VIDEO_NOTE, videoNote.getFileId(), null);
        }
        Video video = message.getVideo();
        if (Objects.nonNull(video)) {
            return new FileDto(FileTypes.VIDEO, video.getFileId(), video.getFileName());
        }
        Voice voice = message.getVoice();
        if (Objects.nonNull(voice)) {
            return new FileDto(FileTypes.VOICE, voice.getFileId(), null);
        }
        Animation animation = message.getAnimation();
        if (Objects.nonNull(animation)) {
            return new FileDto(FileTypes.ANIMATION, animation.getFileId(), animation.getFileName());
        }
        Audio audio = message.getAudio();
        if (Objects.nonNull(audio)) {
            return new FileDto(FileTypes.AUDIO, audio.getFileId(), audio.getFileName());
        }
        return null;
    }

}
