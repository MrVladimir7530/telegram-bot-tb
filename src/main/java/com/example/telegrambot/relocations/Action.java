package com.example.telegrambot.relocations;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

@Service
public interface Action {
    List<SendMessage> doing(Update update);

    InlineKeyboardMarkup createInlineKeyboardMarkup();
}
