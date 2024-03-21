package com.example.telegrambot.relocations;

import com.example.telegrambot.services.SendMessageBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Primary
public class StartMenu implements Action {
    private final SendMessageBot sendMessageBot;

    private final String VIOLATION = "VIOLATION";
    private final String LIST = "LIST";


    @Autowired
    public StartMenu(SendMessageBot sendMessageBot) {
        this.sendMessageBot = sendMessageBot;
    }

    @Override
    public Action doing(Update update) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(createInlineKeyboardMarkup());
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText("Hi, vova");


        return choiceWay(sendMessage);

    }

    public InlineKeyboardMarkup createInlineKeyboardMarkup() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton addedViolation = new InlineKeyboardButton("Добавить нарушение");
        InlineKeyboardButton addedList = new InlineKeyboardButton("Список нарушений");

        addedViolation.setCallbackData(VIOLATION);
        addedList.setCallbackData(LIST);

        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(addedViolation);
        buttons.add(addedList);

        inlineKeyboardMarkup.setKeyboard(Collections.singletonList(buttons));
        return inlineKeyboardMarkup;
    }


    public Action choiceWay(SendMessage sendMessage) {
        sendMessageBot.sendMessage(sendMessage);
        return this;
    }
}
