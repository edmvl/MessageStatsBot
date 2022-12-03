package com.pathz.tgbot.messageStatBot.message_executor;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface MessageExecutor {

    void sendMessage(SendMessage sendMessage);

    ChatMember searchUsersInChat(String chatId, String userId);

    void deleteMessage(Long chatId, Integer messageId);

}
