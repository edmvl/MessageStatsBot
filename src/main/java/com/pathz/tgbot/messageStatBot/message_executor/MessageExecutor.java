package com.pathz.tgbot.messageStatBot.message_executor;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.User;

public interface MessageExecutor {

    void sendMessage(SendMessage sendMessage);

    User searchUsersInChat(String chatId, String userId);

    void deleteMessage(Long chatId, Integer messageId);

}
