package com.example.telegrambot.relocations;

import org.jvnet.hk2.annotations.Service;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public interface StateBot {
    StateBot doing(Update update);
}
