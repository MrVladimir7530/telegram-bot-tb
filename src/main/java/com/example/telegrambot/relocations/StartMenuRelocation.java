package com.example.telegrambot.relocations;

import com.example.telegrambot.model.SendMessageAndStateBot;
import com.example.telegrambot.repositories.ViolationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class StartMenuRelocation implements StateBot {
    private final ViolationRelocation violationRelocation;
    private final ListViolationsRelocation listViolationsRelocation;
    private final ViolationRepository violationRepository;

    private final String VIOLATION = "VIOLATION";
    private final String LIST_VIOLATION = "LIST_VIOLATION";


    @Autowired
    public StartMenuRelocation(ViolationRelocation violationRelocation, ListViolationsRelocation listViolationsRelocation, ViolationRepository violationRepository) {
        this.violationRelocation = violationRelocation;
        this.listViolationsRelocation = listViolationsRelocation;
        this.violationRepository = violationRepository;
    }

    @Override
    public SendMessageAndStateBot doing(Update update) {
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            return choiceWayForButton(callbackQuery);
        }

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId());
        return choiceWayForText(sendMessage);

    }

    @Override
    public SendMessage createKeyboard(SendMessage sendMessage) {
        InlineKeyboardMarkup inlineKeyboardMarkup = createInlineKeyboardMarkup();
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;

    }

    public InlineKeyboardMarkup createInlineKeyboardMarkup() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton addViolation = new InlineKeyboardButton("Добавить нарушение");
        InlineKeyboardButton addedText = new InlineKeyboardButton("Список нарушений");

        addViolation.setCallbackData(VIOLATION);
        addedText.setCallbackData(LIST_VIOLATION);

        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(addViolation);
        buttons.add(addedText);

        inlineKeyboardMarkup.setKeyboard(Collections.singletonList(buttons));
        return inlineKeyboardMarkup;
    }


    public SendMessageAndStateBot choiceWayForText(SendMessage sendMessage) {
        sendMessage.setText("Пожалуста, выберите кнопку");
        return getSendMessageAndStateBot(this, sendMessage);
    }

    public SendMessageAndStateBot choiceWayForButton(CallbackQuery callbackQuery) {
        Message message = callbackQuery.getMessage();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        switch (callbackQuery.getData()) {
            case VIOLATION:
                sendMessage.setText("укажите текст и фото нарушения");
                return getSendMessageAndStateBot(violationRelocation, sendMessage);
            case LIST_VIOLATION:
                sendMessage.setText("Список замеченный вами нарушений:");
                //todo написать получение всех нарушения по chat_id
                return getSendMessageAndStateBot(listViolationsRelocation, sendMessage);
            default:
                return getSendMessageAndStateBot(this, sendMessage);
        }
    }

    public SendMessageAndStateBot getSendMessageAndStateBot(StateBot stateBot, SendMessage message) {
        SendMessageAndStateBot sendMessageAndStateBot = new SendMessageAndStateBot();
        sendMessageAndStateBot.setSendMessage(message);
        sendMessageAndStateBot.setStateBot(stateBot);
        return sendMessageAndStateBot;
    }
}
