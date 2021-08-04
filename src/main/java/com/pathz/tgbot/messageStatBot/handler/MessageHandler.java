package com.pathz.tgbot.messageStatBot.handler;

import com.pathz.tgbot.messageStatBot.message_sender.MessageSender;
import com.pathz.tgbot.messageStatBot.service.StatsService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class MessageHandler implements Handler<Message> {

    private final MessageSender messageSender;
    private final StatsService statsService;

    private static final String STATS_COMMAND = "/msg_stat";
    private static final String GET_MOST_FREQ_WORD = "/top_word";

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

            if (!userText.equals(STATS_COMMAND) && !userText.equals(GET_MOST_FREQ_WORD)) {
                statsService.processStatistic(userText);
            }

            if (userText.equals(GET_MOST_FREQ_WORD)) {
                SendMessage sendMessage = SendMessage.builder()
                        .text("The most frequency word is <b>" + statsService.getMostFrequencyWord()+"</b>")
                        .parseMode("HTML")
                        .chatId(String.valueOf(message.getChatId()))
                        .build();

                messageSender.sendMessage(sendMessage);
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
