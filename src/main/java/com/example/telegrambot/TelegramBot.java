package com.example.telegrambot;

import com.example.telegrambot.configurations.BotConfig;
import com.example.telegrambot.relocations.StartMenuRelocation;
import com.example.telegrambot.relocations.StartMenuAdminRelocation;
import com.example.telegrambot.relocations.StateBot;
import com.example.telegrambot.repositories.AdminRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Service
public class TelegramBot extends TelegramLongPollingBot {

    private StateBot stateBot;
    private final AdminRepository adminRepository;
    private final BotConfig botConfig;
    private final StartMenuRelocation startMenuRelocation;
    private final StartMenuAdminRelocation startMenuAdminRelocation;

    @Autowired
    public TelegramBot(AdminRepository adminRepository, BotConfig botConfig, @Lazy StartMenuRelocation startMenuRelocation, @Lazy StartMenuAdminRelocation startMenuAdminRelocation) {
        this.adminRepository = adminRepository;
        this.botConfig = botConfig;
        this.startMenuRelocation = startMenuRelocation;
        this.startMenuAdminRelocation = startMenuAdminRelocation;
    }


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            checkByAdmin(chatId);
        } else {
            Long chatId = update.getMessage().getChatId();
            checkByAdmin(chatId);
        }
        if (stateBot == null) {
            stateBot = startMenuRelocation;
        }
        stateBot = stateBot.doing(update);
    }

    private void checkByAdmin(Long chatId) {
        if (adminRepository.getAdmins().contains(chatId) && stateBot == null) {
            stateBot = startMenuAdminRelocation;
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }
}
