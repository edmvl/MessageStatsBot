package com.pathz.tgbot.messageStatBot.service;

import com.pathz.tgbot.messageStatBot.dto.MessageDTO;

public interface AnswerCheckable {
    void checkAnswer(MessageDTO messageDTO);
}
