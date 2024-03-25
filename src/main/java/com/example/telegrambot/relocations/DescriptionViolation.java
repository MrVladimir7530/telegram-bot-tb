package com.example.telegrambot.relocations;

import com.example.telegrambot.entity.Violation;
import com.example.telegrambot.repositories.ViolationRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class DescriptionViolation implements Action {
    private StateViolation stateViolation;
    private Violation violation;
    private final ViolationRepository violationRepository;
    private final StateBot stateBot;
    private final StartMenu startMenu;
    private final String BACK = "BACK";
    private final String START = "START";

    public DescriptionViolation(ViolationRepository violationRepository, @Lazy StateBot stateBot, @Lazy StartMenu startMenu) {
        this.violationRepository = violationRepository;
        this.stateBot = stateBot;
        this.startMenu = startMenu;
        stateViolation = StateViolation.DESCRIPTION;
        violation = new Violation();
    }

    @Override
    public List<SendMessage> doing(Update update) {
        SendMessage sendMessage = new SendMessage();
        List<SendMessage> sendMessages = new ArrayList<>();
        if (update.hasCallbackQuery()) {
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            sendMessage.setChatId(chatId);
            SendMessage messageAtCallbackQuery = getMessageAtCallbackQuery(update, sendMessage);
            sendMessages.add(messageAtCallbackQuery);
            return sendMessages;
        }
        sendMessage.setChatId(update.getMessage().getChatId());
        stateViolation = getStateViolationAtText();
        String text = getText(stateViolation);
        sendMessage.setText(text);
        sendMessages.add(sendMessage);
        return sendMessages;
    }

    @Override
    public InlineKeyboardMarkup createInlineKeyboardMarkup() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> buttons = new ArrayList<>();

        InlineKeyboardButton back = new InlineKeyboardButton("Назад");
        back.setCallbackData(BACK);
        buttons.add(back);

        if (!stateViolation.equals(StateViolation.DESCRIPTION)) {
            InlineKeyboardButton inMenu = new InlineKeyboardButton("В меню");
            inMenu.setCallbackData(START);
            buttons.add(inMenu);
        }

        inlineKeyboardMarkup.setKeyboard(Collections.singletonList(buttons));
        return inlineKeyboardMarkup;
    }

    public SendMessage getMessageAtCallbackQuery(Update update, SendMessage sendMessage) {
        String data = update.getCallbackQuery().getData();
        switch (data) {
            case BACK -> {
                stateViolation = getStateViolation();
                String text = getText(stateViolation);
                sendMessage.setText(text);
                return sendMessage;
            }
            case START -> {
                sendMessage.setText("Вы вернулись в стартовое меню");
                stateViolation = StateViolation.DESCRIPTION;
                stateBot.setAction(startMenu);
                return sendMessage;
            }
        }
        return null;
    }

    public StateViolation getStateViolation() {
        switch (stateViolation) {
            case DESCRIPTION -> {
                stateBot.setAction(startMenu);
                return StateViolation.DESCRIPTION;
            }
            case PLACE -> {
                return StateViolation.DESCRIPTION;
            }
            case PHOTO -> {
                return StateViolation.PLACE;
            }
            case FINAL -> {
                return StateViolation.PHOTO;
            }

        }
        throw new RuntimeException();
    }

    public String getText(StateViolation stateViolation) {
        switch (stateViolation) {
            case DESCRIPTION -> {
                if (stateBot.getAction().equals(this)) {
                    return "Опишите нарушение";
                }
                return "Вы веренулись в стартовое меню";
            }
            case PLACE -> {
                return "Где нарушение";
            }
            case PHOTO -> {
                return "Добавьте фото";
            }
            case FINAL -> {
                return "Итоговый вариант";
            }

        }
        //todo подправить
        throw new RuntimeException();
    }

    public StateViolation getStateViolationAtText() {
        switch (stateViolation) {
            case DESCRIPTION -> {
                return StateViolation.PLACE;
            }
            case PLACE -> {
                return StateViolation.PHOTO;
            }
            case PHOTO, FINAL -> {
                return StateViolation.FINAL;
            }
        }
        //todo подправить
        throw new RuntimeException();
    }

    public void createViolation(Update update) {
        switch (stateViolation) {
            case DESCRIPTION -> {
                String text = update.getMessage().getText();
                violation.setText(text);
            }
            case PLACE -> {
                String text = update.getMessage().getText();
                violation.setPlace(text);
            }
            case PHOTO -> {

            }
            case FINAL -> {
                violationRepository.save(violation);
                violation = new Violation();
            }
        }
    }
}
