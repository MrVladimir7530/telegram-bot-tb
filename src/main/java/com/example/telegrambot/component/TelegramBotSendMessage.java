package com.example.telegrambot.component;

import com.example.telegrambot.TelegramBot;
import liquibase.pro.packaged.S;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@DependsOn("telegramBot")
@Lazy
@Slf4j
public class TelegramBotSendMessage {
    @Autowired
    private final TelegramBot telegramBot;


    public TelegramBotSendMessage(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public Message sendMessageFromUser(SendMessage sendMessage) {
        Message message = new Message();
        try {
            message = telegramBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
        return message;
    }

    public Message sendPhotoFromUser(SendPhoto sendPhoto) {
        Message message = new Message();
        try {
            message = telegramBot.execute(sendPhoto);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
        return message;
    }


}
