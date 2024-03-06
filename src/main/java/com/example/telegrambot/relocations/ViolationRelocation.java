package com.example.telegrambot.relocations;

import com.example.telegrambot.entity.Violation;
import com.example.telegrambot.repositories.ViolationRepository;
import com.example.telegrambot.services.SendMessageBot;
import liquibase.pro.packaged.S;
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
public class ViolationRelocation implements StateBot {
    private final NewViolationRelocation newViolationRelocation;
    private final ViolationRepository violationRepository;
    private Violation violation;
    private final SendMessageBot sendMessageBot;
    private ViolationChoiceWay violationChoiceWay = ViolationChoiceWay.START;
    private final String PHOTO = "VIOLATION";
    private final String TEXT = "TEXT";
    private final String SAVE = "SAVE";
    private final String BACK_TO_START_MENU = "BACK";
    private final String BACK = "BACK_TO_START_MENU";

    public ViolationRelocation(NewViolationRelocation newViolationRelocation, ViolationRepository violationRepository, SendMessageBot sendMessageBot) {
        this.newViolationRelocation = newViolationRelocation;
        this.violationRepository = violationRepository;
        this.sendMessageBot = sendMessageBot;
    }

    @Override
    public StateBot doing(Update update) {
        SendMessage sendMessage = new SendMessage();
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            return choiceWayForButton(callbackQuery);
        }

        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setReplyMarkup(createInlineKeyboardMarkup());
        return choiceWayForText(sendMessage);
    }

    public InlineKeyboardMarkup createInlineKeyboardMarkup() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton addViolation = new InlineKeyboardButton("Добавить фото");
        InlineKeyboardButton addText = new InlineKeyboardButton("Добавить/изменить текст");
        InlineKeyboardButton saveViolation = new InlineKeyboardButton("Сохранить");
        InlineKeyboardButton back = new InlineKeyboardButton("Назад");

        addViolation.setCallbackData(PHOTO);
        addText.setCallbackData(TEXT);
        saveViolation.setCallbackData(SAVE);
        back.setCallbackData(BACK_TO_START_MENU);


        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(addViolation);
        buttons.add(addText);
        buttons.add(saveViolation);
        buttons.add(back);

        inlineKeyboardMarkup.setKeyboard(Collections.singletonList(buttons));
        return inlineKeyboardMarkup;
    }
    public InlineKeyboardMarkup createKeyboardBack() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton back = new InlineKeyboardButton("Назад");

        back.setCallbackData(BACK);

        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(back);

        inlineKeyboardMarkup.setKeyboard(Collections.singletonList(buttons));
        return inlineKeyboardMarkup;
    }


    public StateBot choiceWayForText(SendMessage sendMessage) {
        switch (violationChoiceWay) {
            case START -> {
                sendMessage.setReplyMarkup(createInlineKeyboardMarkup());
                sendMessage.setText("Пожалуйста, выберите кнопку");
                sendMessageBot.sendMessage(sendMessage);
                return this;
            }
            case PHOTO -> {
                sendMessage.setReplyMarkup(createKeyboardBack());
                sendMessage.setText("Фото успешно добавлено");
                sendMessageBot.sendMessage(sendMessage);
                return this;
            }
            case TEXT -> {
                sendMessage.setReplyMarkup(createKeyboardBack());
                sendMessage.setText("Текст успешно добавлен");
                sendMessageBot.sendMessage(sendMessage);
                return this;
            }
        }
        return null;
    }

    public StateBot choiceWayForButton(CallbackQuery callbackQuery) {
        Message message = callbackQuery.getMessage();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        switch (callbackQuery.getData()) {
            case PHOTO -> {
                sendMessage.setText("Добавьте фото нарушения");
                sendMessageBot.sendMessage(sendMessage);
                violationChoiceWay = ViolationChoiceWay.PHOTO;
                return newViolationRelocation;
            }
            case TEXT -> {
                sendMessage.setText("Добавьте описание нарушения");
                sendMessageBot.sendMessage(sendMessage);
                violationChoiceWay = ViolationChoiceWay.TEXT;
                return this;
            }
            case SAVE -> {
                violationRepository.save(violation);
                violation = null;
                sendMessage.setText("Нарушение успешно добавлено");
                sendMessageBot.sendMessage(sendMessage);
                return null;
            }
            case BACK_TO_START_MENU -> {
                sendMessage.setText("Вы вернулись в начальное меню");
                sendMessageBot.sendMessage(sendMessage);
                return null;
            }
            case BACK -> {
                sendMessage.setText("укажите текст и фото нарушения");
                sendMessageBot.sendMessage(sendMessage);
                return this;
            }
            default -> {
                return this;
            }
        }
    }
}
