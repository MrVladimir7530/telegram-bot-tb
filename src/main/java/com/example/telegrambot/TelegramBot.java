package com.example.telegrambot;

import com.example.telegrambot.configurations.BotConfig;
import com.example.telegrambot.relocations.Action;
import com.example.telegrambot.relocations.StateBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {
    private final StateBot stateBot;
    private final BotConfig botConfig;
    private Message message;

    @Override
    public void onUpdateReceived(Update update) {
        Action action = stateBot.getAction();
        SendMessage sendMessage = action.doing(update);

        Action newAction = stateBot.getAction();
        InlineKeyboardMarkup inlineKeyboardMarkup = newAction.createInlineKeyboardMarkup();
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        sendMessageFromUser(sendMessage);
    }

    private void sendMessageFromUser(SendMessage sendMessage) {
        try {
            if (message != null) {
                deleteMessageFromUser();
            }
            message = execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private void deleteMessageFromUser() throws TelegramApiException {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(message.getChatId());
        deleteMessage.setMessageId(message.getMessageId());
        execute(deleteMessage);
    }


    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }
}
