package com.pathz.tgbot.messageStatBot.service;

import com.pathz.tgbot.messageStatBot.dto.MessageDTO;

public interface CommandExecutable {
    void executeCommand(MessageDTO messageDTO);
}
