package com.example.telegrambot.relocations;

import com.example.telegrambot.services.SendMessageBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class StartMenuRelocation implements StateBot {
    private final SendMessageBot sendMessageBot;
    private final ViolationRelocation violationRelocation;

    private final String VIOLATION = "VIOLATION";
    private final String LIST_VIOLATION = "LIST_VIOLATION";


    @Autowired
    public StartMenuRelocation(SendMessageBot sendMessageBot, ViolationRelocation violationRelocation) {
        this.sendMessageBot = sendMessageBot;
        this.violationRelocation = violationRelocation;
    }

    @Override
    public StateBot doing(Update update) {
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            return choiceWayForButton(callbackQuery);
        }

        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(createInlineKeyboardMarkup());
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText("Hi, vova");


        return choiceWayForText(sendMessage);

    }

    public InlineKeyboardMarkup createInlineKeyboardMarkup() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton addViolation = new InlineKeyboardButton("Добавить нарушение");
        InlineKeyboardButton addedText = new InlineKeyboardButton("Список нарушений");

        addViolation.setCallbackData(VIOLATION);
        addedText.setCallbackData(LIST_VIOLATION);

        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(addViolation);
        buttons.add(addedText);

        inlineKeyboardMarkup.setKeyboard(Collections.singletonList(buttons));
        return inlineKeyboardMarkup;
    }


    public StateBot choiceWayForText(SendMessage sendMessage) {
        String text = sendMessage.getText();
        sendMessageBot.sendMessage(sendMessage);
        return this;
    }

    public StateBot choiceWayForButton(CallbackQuery callbackQuery) {
        Message message = callbackQuery.getMessage();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        switch (callbackQuery.getData()) {
            case VIOLATION:
                sendMessage.setText("укажите текст и фото нарушения");
                InlineKeyboardMarkup inlineKeyboardMarkup = violationRelocation.createInlineKeyboardMarkup();
                sendMessage.setReplyMarkup(inlineKeyboardMarkup);
                sendMessageBot.sendMessage(sendMessage);
                return violationRelocation;
            case LIST_VIOLATION:
                sendMessage.setText("Список замеченный вами нарушений:");
                sendMessageBot.sendMessage(sendMessage);
                return this;
            default:
                return this;
        }
    }
}
