package com.example.telegrambot.relocations;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class Relocation {
    private StateBot stateBot;

    public Relocation(@Lazy StateBot stateBot) {
        this.stateBot = stateBot;
    }

    public void choiceWay(Update update) {
        Action action = stateBot.getAction();
        action.doing(update);
    }
}
