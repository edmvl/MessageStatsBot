package com.pathz.tgbot.messageStatBot.service;

import com.pathz.tgbot.messageStatBot.dto.MessageDTO;
import com.pathz.tgbot.messageStatBot.entity.Remind;
import com.pathz.tgbot.messageStatBot.message_executor.MessageExecutor;
import com.pathz.tgbot.messageStatBot.repo.RemindRepo;
import com.pathz.tgbot.messageStatBot.util.enums.BotCommands;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RemindService implements CommandExecutable {
    private final MessageExecutor messageExecutor;
    private final RemindRepo remindRepo;
    private final StatsService statsService;

    public RemindService(MessageExecutor messageExecutor, RemindRepo remindRepo, StatsService statsService) {
        this.messageExecutor = messageExecutor;
        this.remindRepo = remindRepo;
        this.statsService = statsService;
    }

    public void addReminder(Long chatId, Long userId, Integer replyMessageId, String text) {
        Remind remind = new Remind();
        remind.setReplyMessageId(replyMessageId.toString());
        remind.setChatId(chatId.toString());
        remind.setUserId(userId.toString());
        remind.setText(text);
        remind.setActive(true);
        remindRepo.save(remind);
    }

    @Override
    public void executeCommand(MessageDTO messageDTO) {
        String userText = messageDTO.getUserText();
        String reminderText = userText.replaceFirst(BotCommands.REMINDER.getCommand(), "");
        if (userText.startsWith(BotCommands.REMINDER.getCommand())) {
            addReminder(messageDTO.getChatId(), messageDTO.getUserId(), messageDTO.getReplyMessageId(), reminderText);
            messageExecutor.sendMessage(messageDTO.getChatId(), reminderText, messageDTO.getReplyMessageId());
        }
    }

    public void playReminder() {
        List<String> distinctChatId = statsService.findAllChats();
        distinctChatId.forEach(s -> {
            List<Remind> allByActiveIsTrueAndChatId = remindRepo.findAllByActiveIsTrueAndChatId(s);
            allByActiveIsTrueAndChatId.forEach(remind -> messageExecutor.sendMessage(remind.getChatId(), remind.getText(), Integer.valueOf(remind.getReplyMessageId())));
        });
    }
}
