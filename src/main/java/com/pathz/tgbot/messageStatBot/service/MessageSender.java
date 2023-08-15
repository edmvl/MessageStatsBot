package com.pathz.tgbot.messageStatBot.service;

import com.pathz.tgbot.messageStatBot.message_executor.MessageExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
public class MessageSender {

    @Autowired
    private MessageExecutor messageExecutor;

    protected void sendMessage(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        messageExecutor.sendMessage(sendMessage);
    }

    protected void sendMessage(String chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        messageExecutor.sendMessage(sendMessage);
    }

}
