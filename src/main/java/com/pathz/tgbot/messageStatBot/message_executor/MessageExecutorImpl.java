package com.pathz.tgbot.messageStatBot.message_executor;

import com.pathz.tgbot.messageStatBot.app.MyTelegramBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Objects;

@Service
public class MessageExecutorImpl implements MessageExecutor {

    private MyTelegramBot tgBot;

    @Override
    public void sendMessage(SendMessage sendMessage) {
        try {
            tgBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public User searchUsersInChat(String chatId, String userId) {
        GetChatMember getChatMember = new GetChatMember();
        getChatMember.setChatId(chatId);
        getChatMember.setUserId(Long.valueOf(userId));
        try {
            ChatMember chatMember = tgBot.execute(getChatMember);
            return Objects.nonNull(chatMember) ? chatMember.getUser() : null;
        } catch (TelegramApiException e) {
            return null;
        }
    }

    @Override
    public void deleteMessage(Long chatId, Integer messageId) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(String.valueOf(chatId));
        deleteMessage.setMessageId(messageId);
        try {
            tgBot.execute(deleteMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Autowired
    public void setMyTelegramBot(MyTelegramBot tgBot) {
        this.tgBot = tgBot;
    }

}
