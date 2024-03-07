package com.example.telegrambot.model;

import com.example.telegrambot.relocations.StateBot;
import lombok.Data;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Data
public class SendMessageAndStateBot {
    private SendMessage sendMessage;
    private StateBot stateBot;
}
