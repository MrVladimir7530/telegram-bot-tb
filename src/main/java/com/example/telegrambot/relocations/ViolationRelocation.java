package com.example.telegrambot.relocations;

import com.example.telegrambot.entity.Violation;
import com.example.telegrambot.model.SendMessageAndStateBot;
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
public class ViolationRelocation  implements StateBot {
    private final ViolationRepository violationRepository;
    private Violation violation;
    private ViolationChoiceWay violationChoiceWay = ViolationChoiceWay.START;
    private final String PHOTO = "VIOLATION";
    private final String TEXT = "TEXT";
    private final String SAVE = "SAVE";
    private final String BACK_TO_START_MENU = "BACK";
    private final String BACK = "BACK_TO_START_MENU";

    public ViolationRelocation(ViolationRepository violationRepository) {
        this.violationRepository = violationRepository;;
    }

    @Override
    public SendMessageAndStateBot doing(Update update) {
        SendMessage sendMessage = new SendMessage();
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            return choiceWayForButton(callbackQuery);
        }

        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setReplyMarkup(createInlineKeyboardMarkup());
        return choiceWayForText(sendMessage);
    }

    @Override
    public SendMessage createKeyboard(SendMessage sendMessage) {
        InlineKeyboardMarkup inlineKeyboardMarkup = null;
        switch (violationChoiceWay) {
            case START -> inlineKeyboardMarkup = createInlineKeyboardMarkup();
            case TEXT, PHOTO -> inlineKeyboardMarkup = createKeyboardBack();
            default -> createInlineKeyboardMarkup();
        }

        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
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

        List<List<InlineKeyboardButton>> rowLine = new ArrayList<>();
        List<InlineKeyboardButton> buttons1 = new ArrayList<>();
        List<InlineKeyboardButton> buttons2 = new ArrayList<>();
        List<InlineKeyboardButton> buttons3 = new ArrayList<>();
        List<InlineKeyboardButton> buttons4 = new ArrayList<>();

        buttons1.add(addViolation);
        buttons2.add(addText);
        buttons3.add(saveViolation);
        buttons4.add(back);

        rowLine.add(buttons1);
        rowLine.add(buttons2);
        rowLine.add(buttons3);
        rowLine.add(buttons4);

        inlineKeyboardMarkup.setKeyboard(rowLine);
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


    public SendMessageAndStateBot choiceWayForText(SendMessage sendMessage) {
        switch (violationChoiceWay) {
            case START -> {
                sendMessage.setText("Пожалуйста, выберите кнопку");
                return getSendMessageAndStateBot(this, sendMessage);
            }
            case PHOTO -> {
                sendMessage.setText("Фото успешно добавлено");
                return getSendMessageAndStateBot(this, sendMessage);
            }
            case TEXT -> {
                sendMessage.setText("Текст успешно добавлен");
                return getSendMessageAndStateBot(this, sendMessage);
            }
        }
        return getSendMessageAndStateBot(this, sendMessage);
    }

    public SendMessageAndStateBot choiceWayForButton(CallbackQuery callbackQuery) {
        Message message = callbackQuery.getMessage();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        switch (callbackQuery.getData()) {
            case PHOTO -> {
                violationChoiceWay = ViolationChoiceWay.PHOTO;
                sendMessage.setText("Добавьте фото нарушения");
                return getSendMessageAndStateBot(this, sendMessage);
            }
            case TEXT -> {
                violationChoiceWay = ViolationChoiceWay.TEXT;
                sendMessage.setText("Добавьте описание нарушения");
                return getSendMessageAndStateBot(this, sendMessage);
            }
            case SAVE -> {
                violationRepository.save(violation);
                violation = null;
                sendMessage.setText("Нарушение успешно добавлено");
                return getSendMessageAndStateBot(null, sendMessage);
            }
            case BACK_TO_START_MENU -> {
                sendMessage.setText("Вы вернулись в начальное меню");
                return getSendMessageAndStateBot(null, sendMessage);
            }
            case BACK -> {
                sendMessage.setText("укажите текст и фото нарушения");
                return getSendMessageAndStateBot(null, sendMessage);
            }
            default -> {
                return getSendMessageAndStateBot(null, sendMessage);
            }
        }
    }

    public SendMessageAndStateBot getSendMessageAndStateBot(StateBot stateBot, SendMessage message) {
        SendMessageAndStateBot sendMessageAndStateBot = new SendMessageAndStateBot();
        sendMessageAndStateBot.setSendMessage(message);
        sendMessageAndStateBot.setStateBot(stateBot);
        return sendMessageAndStateBot;
    }
}
