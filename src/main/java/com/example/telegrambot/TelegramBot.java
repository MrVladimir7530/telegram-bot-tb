package com.example.telegrambot;

import com.example.telegrambot.configurations.BotConfig;
import com.example.telegrambot.relocations.Action;
import com.example.telegrambot.relocations.StateBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Service

public class TelegramBot extends TelegramLongPollingBot {
    private StateBot stateBot;
    private final BotConfig botConfig;

    public TelegramBot(StateBot stateBot, BotConfig botConfig) {
        this.stateBot = stateBot;
        this.botConfig = botConfig;
    }


    @Override
    public void onUpdateReceived(Update update) {
        Action action = stateBot.getAction();
        action.doing(update);

    }

//    private void sendMessageFromUser(SendMessage sendMessage) {
//        try {
//            Message message = execute(sendMessage);
//            messages.add(message);
//        } catch (TelegramApiException e) {
//            log.error(e.getMessage());
//        }
//    }
//
//    private void deleteMessageFromUser(Update update)  {
//        if (!update.hasCallbackQuery()) {
//            Message messageFromUser = update.getMessage();
//            messages.add(messageFromUser);
//        }
//        for (Message message : messages) {
//            DeleteMessage deleteMessage = new DeleteMessage();
//            deleteMessage.setChatId(message.getChatId());
//            deleteMessage.setMessageId(message.getMessageId());
//            try {
//                execute(deleteMessage);
//            } catch (TelegramApiException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        messages = new ArrayList<>();
//
//    }


    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }
}
