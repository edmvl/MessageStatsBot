package com.pathz.tgbot.messageStatBot.message_sender;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface MessageSender {

    void sendMessage(SendMessage sendMessage);
}
