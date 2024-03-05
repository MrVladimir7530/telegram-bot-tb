package com.example.telegrambot;

import com.example.telegrambot.configurations.BotConfig;
import com.example.telegrambot.relocations.StartMenu;
import com.example.telegrambot.relocations.StateBot;
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
    private final BotConfig botConfig;
    private final StartMenu startMenu;

    @Autowired
    public TelegramBot(BotConfig botConfig, @Lazy StartMenu startMenu) {
        this.botConfig = botConfig;
        this.startMenu = startMenu;
    }


    @Override
    public void onUpdateReceived(Update update) {
        if (stateBot == null) {
            stateBot = startMenu;
        }
        stateBot = stateBot.doing(update);
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
