package com.pathz.tgbot.messageStatBot.handler;

import com.pathz.tgbot.messageStatBot.entity.Stats;
import com.pathz.tgbot.messageStatBot.entity.Stinky;
import com.pathz.tgbot.messageStatBot.message_executor.MessageExecutor;
import com.pathz.tgbot.messageStatBot.service.StatsService;
import com.pathz.tgbot.messageStatBot.service.StinkyService;
import com.pathz.tgbot.messageStatBot.util.BotCommands;
import com.pathz.tgbot.messageStatBot.util.MessageFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.User;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

@Component
public class MessageHandler implements Handler<Message> {

    private final MessageExecutor messageExecutor;
    private final StatsService statsService;
    private final StinkyService stinkyService;

    private final Logger logger = Logger.getLogger("MessageHandler");

    @Value("${telegram.bot.username}")
    private String botUsername;
    @Lazy
    public MessageHandler(MessageExecutor messageExecutor, StatsService service, StinkyService stinkyService) {
        this.messageExecutor = messageExecutor;
        this.statsService = service;
        this.stinkyService = stinkyService;
    }

    @Override
    public void choose(Message message) {
        User sender = message.getFrom();
        String from = MessageFormatter.trimNull(sender.getFirstName(), sender.getLastName(), "(" + sender.getUserName() + ")");
        System.out.println(from + " : " + message.getText());
        Long chatId = message.getChatId();
        Integer messageId = message.getMessageId();
        Long userId = message.getFrom().getId();
        if (message.hasText()) {
            String userText = message.getText();
            String userName = message.getFrom().getUserName();

            if (!userText.contains("/")) {
                statsService.processStatistic(chatId.toString(), userId.toString(), userName, from);
            }
            userText = String.join("", userText.split("@" + botUsername));
            if (userText.equals(BotCommands.HELP_COMMAND.getCommand())) {
                send(message, statsService.getHelp());
            }

            if (userText.equals(BotCommands.GET_STINKY_ASS.getCommand())) {
                stinkyService.sendStinky(chatId, messageId);
            }

            if (userText.equals(BotCommands.GET_STATS_ALL.getCommand())) {
                statsService.sendStats(chatId, messageId);
            }

            if (userText.equals(BotCommands.GET_CHATTY.getCommand())) {
                statsService.sendChatty(chatId, messageId);
            }
            if (userText.equals(BotCommands.GET_CHATTY_DAYS.getCommand())) {
                statsService.sendChattyDays(chatId, messageId);
            }
            if (userText.equals(BotCommands.SKIP_STATS.getCommand())) {
                statsService.skipStats(chatId, userId, messageId);
            }
        }
        statsService.processNewChatMembers(message);
        statsService.processLeftChatMembers(message);
    }

    private void send(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(message.getChatId()));
        sendMessage.setText(text);
        messageExecutor.sendMessage(sendMessage);
    }

}
