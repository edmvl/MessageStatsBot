package com.pathz.tgbot.messageStatBot.repo;

import com.pathz.tgbot.messageStatBot.entity.ChatSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatSettingsRepo extends JpaRepository<ChatSettings, Long> {
    ChatSettings findFirstByChatIdAndSettingName(String chatId, String settingName);
    ChatSettings findFirstBySettingName(String settingName);
}
