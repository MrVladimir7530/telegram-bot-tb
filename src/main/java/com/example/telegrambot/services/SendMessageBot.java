package com.example.telegrambot.services;

import com.example.telegrambot.TelegramBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
//@DependsOn("telegramBot")
public class SendMessageBot {
    private final TelegramBot telegramBot;

    @Autowired
    public SendMessageBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }


    public void sendMessage(SendMessage sendMessage) {

        try {
            telegramBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
