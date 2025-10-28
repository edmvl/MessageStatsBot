package com.pathz.tgbot.messageStatBot.service;

import com.pathz.tgbot.messageStatBot.dto.ChatViewDto;
import com.pathz.tgbot.messageStatBot.dto.FileDto;
import com.pathz.tgbot.messageStatBot.dto.MessageDTO;
import com.pathz.tgbot.messageStatBot.entity.Log;
import com.pathz.tgbot.messageStatBot.message_executor.MessageExecutor;
import com.pathz.tgbot.messageStatBot.repo.LogRepo;
import com.pathz.tgbot.messageStatBot.util.enums.BotCommands;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Service
@lombok.extern.java.Log
public class LogService implements CommandExecutable {

    private final LogRepo logRepo;

    public LogService(LogRepo logRepo) {
        this.logRepo = logRepo;
    }

     public List<Log> findByChatId(String chatId, int page, int size) {
        return  logRepo.getLogByChatIdOrderByDateTimeDesc(chatId, PageRequest.of(page, size)).toList();
    }

    public List<ChatViewDto> getAllChats() {
        return logRepo.getAllChats();
    }

    @Override
    public void executeCommand(MessageDTO messageDTO) {
    }

    public void save(MessageDTO messageDTO, FileDto file) {
        log.log(Level.INFO,
                String.format("%s %s %s", messageDTO.getChatName(), messageDTO.getUserName(), messageDTO.getUserText()));
        Log log = new Log();
        log.setChatId(String.valueOf(messageDTO.getChatId()));
        log.setChatName(messageDTO.getChatName());
        log.setUserId(String.valueOf(messageDTO.getUserId()));
        log.setUserName(messageDTO.getUserName());
        log.setUserFirstName(messageDTO.getUserFirstName());
        log.setUserLastName(messageDTO.getUserLastName());
        log.setDateTime(messageDTO.getDateTime());
        log.setText(messageDTO.getUserText());
        log.setLoggerType("chatbot");
        if (Objects.nonNull(file)) {
            log.setFileType(file.getFileType().getFileType());
            log.setFile(file.getFileId());
        }
        logRepo.save(log);
    }
}
