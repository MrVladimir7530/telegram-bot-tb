package com.example.telegrambot.relocations;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public interface Action {
    SendMessage doing(Update update);
}
