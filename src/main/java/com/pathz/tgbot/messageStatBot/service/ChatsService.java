package com.pathz.tgbot.messageStatBot.service;

import com.pathz.tgbot.messageStatBot.dto.MessageDTO;
import com.pathz.tgbot.messageStatBot.entity.Taxi;
import com.pathz.tgbot.messageStatBot.message_executor.MessageExecutor;
import com.pathz.tgbot.messageStatBot.repo.TaxiRepo;
import com.pathz.tgbot.messageStatBot.util.enums.BotCommands;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;

import java.util.List;

@Service
public class ChatsService implements CommandExecutable{

    private final MessageExecutor messageExecutor;

    public ChatsService(MessageExecutor messageExecutor) {
        this.messageExecutor = messageExecutor;
    }

    @Override
    public void executeCommand(MessageDTO messageDTO) {
        if (messageDTO.getUserText().startsWith(BotCommands.CHATS.getCommand())) {
            messageExecutor.deleteMessage(messageDTO.getChatId(), messageDTO.getMessageId());
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText("Запустить приложение");
            inlineKeyboardButton.setWebApp(WebAppInfo.builder()
                    .url("https://api.zhendozzz.ru/app/chats")
                    .build());
            inlineKeyboardMarkup.setKeyboard(List.of(List.of(inlineKeyboardButton)));
            SendMessage sendMessage = new SendMessage();
            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
            sendMessage.setChatId(messageDTO.getChatId());
            sendMessage.setText("Поиск такси");
            messageExecutor.sendMessage(sendMessage);
        }
    }

}
