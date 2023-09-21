package com.pathz.tgbot.messageStatBot.service;

import com.pathz.tgbot.messageStatBot.entity.ChatSettings;
import com.pathz.tgbot.messageStatBot.entity.Settings;
import com.pathz.tgbot.messageStatBot.repo.ChatSettingsRepo;
import com.pathz.tgbot.messageStatBot.repo.SettingsRepo;
import com.pathz.tgbot.messageStatBot.util.enums.ChatSettingConstants;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class SettingsService {

    private final ChatSettingsRepo chatSettingsRepo;
    private final SettingsRepo settingsRepo;

    public SettingsService(ChatSettingsRepo chatSettingsRepo, SettingsRepo settingsRepo) {
        this.chatSettingsRepo = chatSettingsRepo;
        this.settingsRepo = settingsRepo;
    }

    public boolean isEnabled(String chatId, ChatSettingConstants name) {
        ChatSettings setting = chatSettingsRepo.findFirstByChatIdAndSettingName(chatId, name.getDbValue());
        return Objects.nonNull(setting) && "true".equals(setting.getSettingValue());
    }

    public boolean isDisabled(String chatId, ChatSettingConstants name) {
        return !isEnabled(chatId, name);
    }

    public boolean isUserAdmin(Long chatId, Long userId) {
        Settings settings = settingsRepo.findByChatIdAndUserId(String.valueOf(chatId), String.valueOf(userId));
        return Objects.nonNull(settings) && settings.getIsAdmin();
    }
}
