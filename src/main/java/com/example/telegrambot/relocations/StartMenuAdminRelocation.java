package com.example.telegrambot.relocations;

import com.example.telegrambot.model.SendMessageAndStateBot;
import com.example.telegrambot.services.SendMessageBot;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class StartMenuAdminRelocation implements StateBot {

    private final SendMessageBot sendMessageBot;

    private final String PHOTO = "Photo";
    private final String TEXT = "TEXT";

    public StartMenuAdminRelocation(SendMessageBot sendMessageBot) {
        this.sendMessageBot = sendMessageBot;
    }

    @Override
    public SendMessageAndStateBot doing(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText("Hi, vova");

        return choiceWay(sendMessage);

    }

    @Override
    public SendMessage createKeyboard(SendMessage sendMessage) {
        sendMessage.setReplyMarkup(createInlineKeyboardMarkup());
        return sendMessage;
    }

    public InlineKeyboardMarkup createInlineKeyboardMarkup() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton addedPhoto = new InlineKeyboardButton("Добавить фото");
        InlineKeyboardButton addedText = new InlineKeyboardButton("Добавить текст");

        addedPhoto.setCallbackData(PHOTO);
        addedText.setCallbackData(TEXT);

        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(addedPhoto);
        buttons.add(addedText);

        inlineKeyboardMarkup.setKeyboard(Collections.singletonList(buttons));
        return inlineKeyboardMarkup;
    }

    public SendMessageAndStateBot choiceWay(SendMessage sendMessage) {
        SendMessageAndStateBot sendMessageAndStateBot = new SendMessageAndStateBot();
        sendMessageBot.sendMessage(sendMessage);
        sendMessageAndStateBot.setSendMessage(sendMessage);
        sendMessageAndStateBot.setStateBot(this);
        return sendMessageAndStateBot;
    }
}
