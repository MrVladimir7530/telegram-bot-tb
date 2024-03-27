package com.example.telegrambot.relocations;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public class ListViolation implements Action{
    @Override
    public void doing(Update update) {

    }

    @Override
    public InlineKeyboardMarkup createInlineKeyboardMarkup() {
        return null;
    }
}
