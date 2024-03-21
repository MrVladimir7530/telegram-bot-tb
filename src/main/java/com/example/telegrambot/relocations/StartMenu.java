package com.example.telegrambot.relocations;

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
    private StateBot stateBot;
    private final String VIOLATION = "VIOLATION";
    private final String LIST = "LIST";

    public StartMenu(StateBot stateBot) {
        this.stateBot = stateBot;
    }


    @Override
    public SendMessage doing(Update update) {
        SendMessage sendMessage = new SendMessage();
        if (update.hasCallbackQuery()) {

        }
        sendMessage.setReplyMarkup(createInlineKeyboardMarkup());
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText("Hi, vova");

        return sendMessage;

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



}
