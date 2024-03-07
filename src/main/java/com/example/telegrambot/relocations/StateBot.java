package com.example.telegrambot.relocations;

import com.example.telegrambot.model.SendMessageAndStateBot;
import org.jvnet.hk2.annotations.Service;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;


public interface StateBot {
    SendMessageAndStateBot doing(Update update);

    SendMessage createKeyboard(SendMessage sendMessage);
}
