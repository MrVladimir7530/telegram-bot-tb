package com.example.telegrambot.relocations;

import com.example.telegrambot.choise_way.ListViolationsChoiceWay;
import com.example.telegrambot.model.SendMessageAndStateBot;
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
public class ListViolationsRelocation implements StateBot{
    private ListViolationsChoiceWay listViolationsChoiceWay = ListViolationsChoiceWay.BEGIN;
    private String keyBoardWay = "START";
    private final String DELETE_VIOLATION = "DELETE_VIOLATION";
    private final String BACK = "BACK";

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
        InlineKeyboardMarkup inlineKeyboardMarkup = null;
        switch (keyBoardWay) {
            case "START"->{
                inlineKeyboardMarkup = createInlineKeyboardMarkup();
            }
            case "DELETE" ->{
                keyBoardWay = "START";
            }
        }
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }

    public InlineKeyboardMarkup createInlineKeyboardMarkup() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton deleteViolation = new InlineKeyboardButton("Удалить нарушение");
        InlineKeyboardButton back = new InlineKeyboardButton("Назад");

        deleteViolation.setCallbackData(DELETE_VIOLATION);
        back.setCallbackData(BACK);

        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(deleteViolation);
        buttons.add(back);

        inlineKeyboardMarkup.setKeyboard(Collections.singletonList(buttons));
        return inlineKeyboardMarkup;
    }

    public SendMessageAndStateBot choiceWayForText(SendMessage sendMessage) {
        switch (listViolationsChoiceWay) {
            case BEGIN -> {
                sendMessage.setText("Пожалуста, выберите кнопку");
            }
            case DELETE -> {
                //todo удаление из репы
                sendMessage.setText("Сообщение удалено");
                keyBoardWay = "START";
                listViolationsChoiceWay = ListViolationsChoiceWay.BEGIN;
                return getSendMessageAndStateBot(null, sendMessage);
            }
        }
        return getSendMessageAndStateBot(this, sendMessage);
    }

    public SendMessageAndStateBot choiceWayForButton(CallbackQuery callbackQuery) {
        Message message = callbackQuery.getMessage();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        switch (callbackQuery.getData()) {
            case DELETE_VIOLATION:
                sendMessage.setText("Укажите номер нарушения");
                keyBoardWay = "DELETE";
                listViolationsChoiceWay = ListViolationsChoiceWay.DELETE;
                return getSendMessageAndStateBot(this, sendMessage);
            case BACK:
                sendMessage.setText("Вы вернулись в стартовое меню");
                return getSendMessageAndStateBot(null, sendMessage);
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
