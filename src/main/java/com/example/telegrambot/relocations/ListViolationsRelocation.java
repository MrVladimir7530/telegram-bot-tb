package com.example.telegrambot.relocations;

import com.example.telegrambot.model.SendMessageAndStateBot;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class ListViolationsRelocation implements StateBot{
    private final String DELETE_VIOLATION = "DELETE_VIOLATION";
    private final String BACK = "BACK";

    @Override
    public SendMessageAndStateBot doing(Update update) {
        return null;
    }

    @Override
    public SendMessage createKeyboard(SendMessage sendMessage) {
        InlineKeyboardMarkup inlineKeyboardMarkup = createInlineKeyboardMarkup();
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }

    public InlineKeyboardMarkup createInlineKeyboardMarkup() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton deleteViolation = new InlineKeyboardButton("Удалить нарушение");
        InlineKeyboardButton back = new InlineKeyboardButton("Назад");

        deleteViolation.setCallbackData(DELETE_VIOLATION);
        back.setCallbackData(BACK);

        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(deleteViolation);
        buttons.add(back);

        inlineKeyboardMarkup.setKeyboard(Collections.singletonList(buttons));
        return inlineKeyboardMarkup;
    }
}
