package com.pathz.tgbot.messageStatBot.rest;

import com.pathz.tgbot.messageStatBot.entity.Log;
import com.pathz.tgbot.messageStatBot.message_executor.MessageExecutor;
import com.pathz.tgbot.messageStatBot.service.LogService;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

@RestController
@RequestMapping("/")
public class MessageController {

    private final MessageExecutor messageExecutor;
    private final LogService logService;

    public MessageController(
            MessageExecutor messageExecutor,
            LogService logService
    ) {
        this.messageExecutor = messageExecutor;
        this.logService = logService;
    }

    @PostMapping("/send")
    public Integer parse(@RequestBody SendMessage message) {
        return messageExecutor.sendMessage(message);
    }
}
