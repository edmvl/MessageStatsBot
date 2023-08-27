package com.pathz.tgbot.messageStatBot.service;

import com.pathz.tgbot.messageStatBot.entity.ChatSettings;
import com.pathz.tgbot.messageStatBot.repo.ChatSettingsRepo;
import com.pathz.tgbot.messageStatBot.util.ChatSettingConstants;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class SettingsService {

    private final ChatSettingsRepo chatSettingsRepo;

    public SettingsService(ChatSettingsRepo chatSettingsRepo) {
        this.chatSettingsRepo = chatSettingsRepo;
    }

    public boolean isEnabled(String chatId, ChatSettingConstants name) {
        ChatSettings setting = chatSettingsRepo.findFirstByChatIdAndSettingName(chatId, name.getDbValue());
        return Objects.nonNull(setting) && "true".equals(setting.getSettingValue());
    }

    public boolean isDisabled(String chatId, ChatSettingConstants name) {
        return !isEnabled(chatId, name);
    }
}
