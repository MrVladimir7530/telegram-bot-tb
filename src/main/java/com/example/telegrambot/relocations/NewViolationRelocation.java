package com.example.telegrambot.relocations;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class NewViolationRelocation implements StateBot{
    @Override
    public StateBot doing(Update update) {
        return null;
    }
}
