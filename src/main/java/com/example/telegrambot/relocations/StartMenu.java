package com.example.telegrambot.relocations;

import com.example.telegrambot.service.SendMessageBot;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class StartMenu implements StateBot {
    private final SendMessageBot sendMessageBot;

    @Autowired
    public StartMenu(SendMessageBot sendMessageBot) {
        this.sendMessageBot = sendMessageBot;
    }

    @Override
    public StateBot doing(Update update) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(createInlineKeyboardMarkup());
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText("Hi, vova");


        return choiceWay(sendMessage);

    }

    public InlineKeyboardMarkup createInlineKeyboardMarkup() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton addedPhoto = new InlineKeyboardButton("Добавить фото");
        InlineKeyboardButton addedText = new InlineKeyboardButton("Добавить текст");

        addedPhoto.setCallbackData("PHOTO");
        addedText.setCallbackData("TEXT");

        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(addedPhoto);
        buttons.add(addedText);

        inlineKeyboardMarkup.setKeyboard(Collections.singletonList(buttons));
        return inlineKeyboardMarkup;
    }


    public StateBot choiceWay(SendMessage sendMessage) {
        sendMessageBot.sendMessage(sendMessage);
        return this;
    }
}
