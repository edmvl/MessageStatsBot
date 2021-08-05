package com.pathz.tgbot.messageStatBot.handler;

import com.pathz.tgbot.messageStatBot.message_sender.MessageSender;
import com.pathz.tgbot.messageStatBot.service.StatsService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import static com.pathz.tgbot.messageStatBot.util.BotCommands.*;

@Component
public class MessageHandler implements Handler<Message> {

    private final MessageSender messageSender;
    private final StatsService statsService;

    public MessageHandler(MessageSender messageSender, StatsService service) {
        this.messageSender = messageSender;
        this.statsService = service;
    }

    @Override
    public void choose(Message message) {
        if (message.hasText()) {
            String userText = message.getText();

            if (userText.equals(STATS_COMMAND)) {
                send(message, statsService.getStatistic());
            }

            if (!commands.contains(userText)) {
                statsService.processStatistic(userText);
            }

            if (userText.equals(GET_MOST_FREQ_WORD_COMMAND)) {
                SendMessage sendMessage = SendMessage.builder()
                        .text("The most frequency word is <b>" + statsService.getMostFrequencyWord()+"</b>")
                        .parseMode("HTML")
                        .chatId(String.valueOf(message.getChatId()))
                        .build();

                messageSender.sendMessage(sendMessage);
            }

            if (userText.equals(GET_AUTHORS_COMMAND)) {
                send(message, statsService.getAuthors());
            }

            if (userText.equals(HELP_COMMAND)) {
                send(message, statsService.getHelp());
            }
        }
    }

    private void send(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(message.getChatId()));
        sendMessage.setText(text);
        messageSender.sendMessage(sendMessage);
    }

}
