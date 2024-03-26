package com.example.telegrambot.relocations;

import com.example.telegrambot.TelegramBot;
import com.example.telegrambot.entity.Photo;
import com.example.telegrambot.entity.Violation;
import com.example.telegrambot.repositories.ViolationRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Service
public class DescriptionViolation implements Action {
    private StateViolation stateViolation;
    private Violation violation;
    private Photo photo;
    private final ViolationRepository violationRepository;
    private final StateBot stateBot;
    private final StartMenu startMenu;
    private final String BACK = "BACK";
    private final String START = "START";
    private final String SKIP = "SKIP";
    private final TelegramBot telegramBot;

    public DescriptionViolation(ViolationRepository violationRepository, @Lazy StateBot stateBot, @Lazy StartMenu startMenu, @Lazy TelegramBot telegramBot) {
        this.violationRepository = violationRepository;
        this.stateBot = stateBot;
        this.startMenu = startMenu;
        this.telegramBot = telegramBot;
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
        createViolation(update);
        sendMessage.setChatId(update.getMessage().getChatId());
        stateViolation = getStateViolationAtText(update);
        String text = getText(stateViolation);
        sendMessage.setText(text);
        sendMessages.add(sendMessage);
        return sendMessages;
    }

    @Override
    public InlineKeyboardMarkup createInlineKeyboardMarkup() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> buttonsLine1 = new ArrayList<>();
        List<InlineKeyboardButton> buttonsLine2 = new ArrayList<>();

        InlineKeyboardButton back = new InlineKeyboardButton("Назад");
        back.setCallbackData(BACK);
        buttonsLine1.add(back);

        if (!stateViolation.equals(StateViolation.DESCRIPTION)) {
            InlineKeyboardButton inMenu = new InlineKeyboardButton("В меню");
            inMenu.setCallbackData(START);
            buttonsLine1.add(inMenu);
        }

        if (stateViolation.equals(StateViolation.PHOTO)) {
            InlineKeyboardButton skip = new InlineKeyboardButton("Пропустить");
            skip.setCallbackData(SKIP);
            buttonsLine2.add(skip);
        }

        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        buttons.add(buttonsLine1);
        buttons.add(buttonsLine2);

        inlineKeyboardMarkup.setKeyboard(buttons);
        return inlineKeyboardMarkup;
    }

    public SendMessage getMessageAtCallbackQuery(Update update, SendMessage sendMessage) {
        String data = update.getCallbackQuery().getData();
        switch (data) {
            case BACK -> {
                stateViolation = getStateViolationAtBack();
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
            case SKIP ->{
                stateViolation = StateViolation.FINAL;
                String text = getText(stateViolation);
                sendMessage.setText(text);
                return sendMessage;
            }
        }
        return null;
    }

    public StateViolation getStateViolationAtBack() {
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
        //todo подправить
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

    public StateViolation getStateViolationAtText(Update update) {
        switch (stateViolation) {
            case DESCRIPTION -> {
                return StateViolation.PLACE;
            }
            case PLACE -> {
                return StateViolation.PHOTO;
            }
            case PHOTO -> {
                return StateViolation.FINAL;
            }
            case FINAL -> {

                //todo тут отправка финала
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
