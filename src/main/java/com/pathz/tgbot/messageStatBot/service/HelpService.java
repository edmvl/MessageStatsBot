package com.pathz.tgbot.messageStatBot.service;

import com.pathz.tgbot.messageStatBot.dto.MessageDTO;
import com.pathz.tgbot.messageStatBot.message_executor.MessageExecutor;
import com.pathz.tgbot.messageStatBot.util.enums.BotCommands;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
public class HelpService implements CommandExecutable {
    private final MessageExecutor messageExecutor;

    public HelpService(MessageExecutor messageExecutor) {
        this.messageExecutor = messageExecutor;
    }

    @Override
    public void executeCommand(MessageDTO messageDTO) {
        if (messageDTO.getUserText().equals(BotCommands.START_COMMAND.getCommand())) {
            messageExecutor.deleteMessage(messageDTO.getChatId(), messageDTO.getMessageId());
            final String eof = "\n";
            String text = "Привет, вот что я пока умею:" + eof +
                    BotCommands.GET_STATS_ALL.getCommand() + " : " + BotCommands.GET_STATS_ALL.getExplainer() + eof +
                    BotCommands.REMINDER.getCommand() + " : " + BotCommands.REMINDER.getExplainer() + eof +
                    BotCommands.REMOVE_REMINDER.getCommand() + " : " + BotCommands.REMOVE_REMINDER.getExplainer() + eof +
                    BotCommands.QUIZ.getCommand() + " : " + BotCommands.QUIZ.getExplainer() + eof +
                    BotCommands.GET_SELECTED.getCommand() + " : " + BotCommands.GET_SELECTED.getExplainer() + eof;
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(messageDTO.getChatId());
            sendMessage.setText(text);
            messageExecutor.sendMessage(sendMessage);
        }
    }
}
