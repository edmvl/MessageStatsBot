package com.pathz.tgbot.messageStatBot.handler;

import com.pathz.tgbot.messageStatBot.entity.Stats;
import com.pathz.tgbot.messageStatBot.entity.Stinky;
import com.pathz.tgbot.messageStatBot.message_executor.MessageExecutor;
import com.pathz.tgbot.messageStatBot.service.StatsService;
import com.pathz.tgbot.messageStatBot.service.StinkyService;
import com.pathz.tgbot.messageStatBot.util.BotCommands;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
public class MessageHandler implements Handler<Message> {

    private final MessageExecutor messageExecutor;
    private final StatsService statsService;
    private final StinkyService stinkyService;

    private final Logger logger = Logger.getLogger("MessageHandler");

    @Value("${telegram.bot.username}")
    private String botUsername;

    public MessageHandler(MessageExecutor messageExecutor, StatsService service, StinkyService stinkyService) {
        this.messageExecutor = messageExecutor;
        this.statsService = service;
        this.stinkyService = stinkyService;
    }

    @Override
    public void choose(Message message) {
        System.out.print(message.getFrom() + " : ");
        System.out.println(message.getText());
        if (message.hasText()) {
            String userText = message.getText();
            Long chatId = message.getChatId();
            Long userId = message.getFrom().getId();
            String userName = message.getFrom().getUserName();

            if (!userText.contains("/")) {
                statsService.processStatistic(chatId.toString(), userId.toString(), userName);
            }
            userText = String.join("", userText.split("@" + botUsername));
            if (userText.equals(BotCommands.HELP_COMMAND.getCommand())) {
                send(message, statsService.getHelp());
                messageExecutor.deleteMessage(message.getChatId(), message.getMessageId());
            }

            if (userText.equals(BotCommands.GET_STINKY_ASS.getCommand())) {
                Stinky existedStinky = stinkyService.findByMessage(chatId.toString(), LocalDate.now());
                if (Objects.nonNull(existedStinky)){
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setText("Паянхи шăршлă кута тупнă");
                    sendMessage.setChatId(chatId);
                    messageExecutor.sendMessage(sendMessage);
                }else {
                    String stinkyUserId = statsService.getStinky(message);
                    User user = messageExecutor.searchUsersInChat(message.getChatId().toString(), stinkyUserId).getUser();
                    String firstName = user.getFirstName();
                    String lastName = user.getLastName();
                    SendMessage sendMessage = new SendMessage();
                    String text = "Кунăн кучĕ питĕ шăршлă:\n";
                    sendMessage.setChatId(message.getChatId());
                    MessageEntity messageEntity = new MessageEntity();
                    messageEntity.setUser(user);
                    messageEntity.setOffset(text.length());
                    String userIdentityText = firstName + " " + (Objects.nonNull(lastName) ? lastName : "") + "\n";
                    text += userIdentityText;
                    messageEntity.setLength(userIdentityText.length());
                    messageEntity.setType("text_mention");
                    sendMessage.setEntities(List.of(messageEntity));
                    sendMessage.setText(text);
                    stinkyService.save(chatId.toString(), stinkyUserId, LocalDate.now());
                    messageExecutor.sendMessage(sendMessage);
                }
                messageExecutor.deleteMessage(message.getChatId(), message.getMessageId());
            }

            if (userText.equals(BotCommands.GET_STATS_ALL.getCommand())) {
                List<Stats> top = statsService.getTopChattyUserId(message);
                String caption = "Паянхи статистика:\n";
                SendMessage sendMessage = new SendMessage();
                StringBuilder text = new StringBuilder();
                text.append(caption);
                ArrayList<MessageEntity> messageEntities = new ArrayList<>();
                top.forEach(stats -> {
                    User user = messageExecutor.searchUsersInChat(message.getChatId().toString(), stats.getUserId()).getUser();
                    String firstName = user.getFirstName();
                    String lastName = user.getLastName();
                    MessageEntity messageEntity = new MessageEntity();
                    messageEntity.setUser(user);
                    messageEntity.setOffset(text.length());
                    String userIdentityText = firstName + " " + (Objects.nonNull(lastName) ? lastName : "") + "(" + stats.getCount() + ")" + "\n";
                    text.append(userIdentityText);
                    messageEntity.setLength(userIdentityText.length());
                    messageEntity.setType("text_mention");
                    messageEntities.add(messageEntity);
                });
                if (messageEntities.isEmpty()){
                    sendMessage.setText("Тем çирман паян...");
                } else {
                    sendMessage.setEntities(messageEntities);
                    sendMessage.setText(text.toString());
                }
                sendMessage.setChatId(chatId);
                messageExecutor.sendMessage(sendMessage);
                messageExecutor.deleteMessage(chatId, message.getMessageId());
            }

            if (userText.equals(BotCommands.GET_CHATTY.getCommand())) {
                List<Stats> top = statsService.getTop10ChattyUserId(message);
                String caption = "Сурăх тути çиекеннисем:\n";
                SendMessage sendMessage = new SendMessage();
                StringBuilder text = new StringBuilder();
                text.append(caption);
                ArrayList<MessageEntity> messageEntities = new ArrayList<>();
                top.forEach(stats -> {
                    User user = messageExecutor.searchUsersInChat(message.getChatId().toString(), stats.getUserId()).getUser();
                    String firstName = user.getFirstName();
                    String lastName = user.getLastName();
                    MessageEntity messageEntity = new MessageEntity();
                    messageEntity.setUser(user);
                    messageEntity.setOffset(text.length());
                    String userIdentityText = firstName + " " + (Objects.nonNull(lastName) ? lastName : "") + "(" + stats.getCount() + ")" + "\n";
                    text.append(userIdentityText);
                    messageEntity.setLength(userIdentityText.length());
                    messageEntity.setType("text_mention");
                    messageEntities.add(messageEntity);
                });
                if (messageEntities.isEmpty()){
                    sendMessage.setText("Тем çирман паян...");
                } else {
                    sendMessage.setEntities(messageEntities);
                    sendMessage.setText(text.toString());
                }
                sendMessage.setChatId(chatId);
                messageExecutor.sendMessage(sendMessage);
                messageExecutor.deleteMessage(chatId, message.getMessageId());
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
