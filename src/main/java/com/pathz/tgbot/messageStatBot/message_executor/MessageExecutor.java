package com.pathz.tgbot.messageStatBot.message_executor;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.User;

public interface MessageExecutor {

    void sendMessage(SendMessage sendMessage);

    void sendMessage(String chatId, String text);

    void sendMessage(Long chatId, String text);

    void sendMessage(String chatId, String text, Integer replyMessageId);

    void sendMessage(Long chatId, String text, Integer replyMessageId);

    User searchUsersInChat(String chatId, String userId);

    void deleteMessage(Long chatId, Integer messageId);

}
