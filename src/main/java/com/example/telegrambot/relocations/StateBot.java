package com.example.telegrambot.relocations;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Getter
@Setter
public class StateBot {
    private Action action;

    public StateBot(@Lazy StartMenu startMenu) {
        this.action = startMenu;
    }


}
