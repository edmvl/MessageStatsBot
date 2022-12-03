package com.pathz.tgbot.messageStatBot.handler;

import com.pathz.tgbot.messageStatBot.entity.Stats;
import com.pathz.tgbot.messageStatBot.message_executor.MessageExecutor;
import com.pathz.tgbot.messageStatBot.service.StatsService;
import com.pathz.tgbot.messageStatBot.util.BotCommands;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

@Component
public class MessageHandler implements Handler<Message> {

    private final MessageExecutor messageExecutor;
    private final StatsService statsService;

    private final Logger logger = Logger.getLogger("MessageHandler");

    public MessageHandler(MessageExecutor messageExecutor, StatsService service) {
        this.messageExecutor = messageExecutor;
        this.statsService = service;
    }

    @Override
    public void choose(Message message) {
        System.out.println(message);
        if (message.hasText()) {
            String userText = message.getText();
            Long chatId = message.getChatId();
            Long userId = message.getFrom().getId();

            if (!userText.contains("/")) {
                statsService.processStatistic(chatId.toString(), userId.toString());
            }

            if (userText.equals(BotCommands.HELP_COMMAND.getCommand())) {
                send(message, statsService.getHelp());
                messageExecutor.deleteMessage(message.getChatId(), message.getMessageId());
            }

            if (userText.equals(BotCommands.GET_STINKY_ASS.getCommand())) {
                String stinky = statsService.getStinky(message);
                User user = messageExecutor.searchUsersInChat(message.getChatId().toString(), stinky).getUser();
                String firstName = user.getFirstName();
                String lastName = user.getLastName();
                SendMessage sendMessage = new SendMessage();
                String text = "Кунон куче пите шоршло:\n";
                sendMessage.setChatId(message.getChatId());
                MessageEntity messageEntity = new MessageEntity();
                messageEntity.setUser(user);
                messageEntity.setOffset(text.length());
                String userIdentityText = firstName + " " + (Objects.nonNull(lastName) ? lastName : "") + "\n";
                text += userIdentityText;
                messageEntity.setLength(userIdentityText.length());
                messageEntity.setType("text_mention");
                sendMessage.setEntities(List.of(messageEntity));
                sendMessage.setText(text.toString());
                messageExecutor.sendMessage(sendMessage);
                messageExecutor.deleteMessage(message.getChatId(), message.getMessageId());
            }

            if (userText.equals(BotCommands.GET_CHATTY.getCommand())) {
                List<Stats> top = statsService.getTopChattyUserId(message);
                String caption = "Сурох тути щиекеннисем:\n";
                SendMessage sendMessage = new SendMessage();
                StringBuilder text = new StringBuilder();
                text.append(caption);
                ArrayList<MessageEntity> messageEntities = new ArrayList<>();
                top.forEach(stats -> {
                    User user = messageExecutor.searchUsersInChat(message.getChatId().toString(), stats.getUserId()).getUser();
                    String firstName = user.getFirstName();
                    String lastName = user.getLastName();
                    sendMessage.setChatId(message.getChatId());
                    MessageEntity messageEntity = new MessageEntity();
                    messageEntity.setUser(user);
                    messageEntity.setOffset(text.length());
                    String userIdentityText = firstName + " " + (Objects.nonNull(lastName) ? lastName : "") + "(" + stats.getCount() + ")" + "\n";
                    text.append(userIdentityText);
                    messageEntity.setLength(userIdentityText.length());
                    messageEntity.setType("text_mention");
                    messageEntities.add(messageEntity);
                });
                sendMessage.setEntities(messageEntities);
                sendMessage.setText(text.toString());
                messageExecutor.sendMessage(sendMessage);
                messageExecutor.deleteMessage(message.getChatId(), message.getMessageId());
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
