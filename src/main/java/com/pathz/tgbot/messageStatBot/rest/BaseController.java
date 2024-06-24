package com.pathz.tgbot.messageStatBot.rest;

import com.pathz.tgbot.messageStatBot.message_executor.MessageExecutor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@RestController
@RequestMapping("/message")
public class BaseController {

    private final MessageExecutor messageExecutor;

    public BaseController(MessageExecutor messageExecutor) {
        this.messageExecutor = messageExecutor;
    }

    @PostMapping("/send")
    public Integer parse(@RequestBody SendMessage message) {
        return messageExecutor.sendMessage(message);
    }
}
