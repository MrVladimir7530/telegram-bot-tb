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
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;

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

    public boolean deleteMessage(Long chatId, Integer messageId) {
        boolean delete = false;
        DeleteMessage deleteMessage = new DeleteMessage(String.valueOf(chatId), messageId);
        try {
            delete = telegramBot.execute(deleteMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
        return delete;
    }

    public void editMessageText(Long chatId, Integer messageId, String text) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(messageId);
        editMessageText.setText(text);
        try {
             telegramBot.execute(editMessageText);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    public void editMessageReplyMarkup(Long chatId, Integer messageId, InlineKeyboardMarkup inlineKeyboardMarkup) {
        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setChatId(chatId);
        editMessageReplyMarkup.setMessageId(messageId);
        editMessageReplyMarkup.setReplyMarkup(inlineKeyboardMarkup);
        try {
            telegramBot.execute(editMessageReplyMarkup);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }

    }

}
