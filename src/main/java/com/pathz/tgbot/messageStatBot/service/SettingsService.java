package com.pathz.tgbot.messageStatBot.service;

import com.pathz.tgbot.messageStatBot.entity.ChatSettings;
import com.pathz.tgbot.messageStatBot.repo.ChatSettingsRepo;
import com.pathz.tgbot.messageStatBot.util.ChatSettingConstants;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {

    private final ChatSettingsRepo chatSettingsRepo;

    public SettingsService(ChatSettingsRepo chatSettingsRepo) {
        this.chatSettingsRepo = chatSettingsRepo;
    }

    public boolean isEnabled(String chatId, ChatSettingConstants name) {
        ChatSettings setting = chatSettingsRepo.findFirstByChatIdAndSettingName(chatId, name.getDbValue());
        String settingValue = setting.getSettingValue();
        return "true".equals(settingValue);
    }

}
