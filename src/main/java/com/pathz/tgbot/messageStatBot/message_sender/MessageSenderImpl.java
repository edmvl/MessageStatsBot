package com.pathz.tgbot.messageStatBot.message_sender;

import com.pathz.tgbot.messageStatBot.app.MyTelegramBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class MessageSenderImpl implements MessageSender {

    private MyTelegramBot tgBot;

    @Override
    public void sendMessage(SendMessage sendMessage) {
        try {
            tgBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Autowired
    public void setMyTelegramBot(MyTelegramBot tgBot) {
        this.tgBot = tgBot;
    }

}
