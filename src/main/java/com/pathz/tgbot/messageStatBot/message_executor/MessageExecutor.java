package com.pathz.tgbot.messageStatBot.message_executor;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.User;

public interface MessageExecutor {

    Integer sendMessage(SendMessage sendMessage);

    Integer sendMessage(String chatId, String text);

    Integer sendMessage(Long chatId, String text);

    Integer sendMessage(String chatId, String text, Integer replyMessageId);

    Integer sendMessage(Long chatId, String text, Integer replyMessageId);

    User searchUsersInChat(String chatId, String userId);

    void deleteMessage(Long chatId, Integer messageId);

}
