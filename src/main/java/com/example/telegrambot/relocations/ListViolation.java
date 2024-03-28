package com.example.telegrambot.relocations;

import com.example.telegrambot.component.TelegramBotSendMessage;
import com.example.telegrambot.entity.Photo;
import com.example.telegrambot.entity.Subscriber;
import com.example.telegrambot.entity.Violation;
import com.example.telegrambot.repositories.ViolationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ListViolation implements Action {
    private final TelegramBotSendMessage telegramBotSendMessage;
    private final ViolationRepository violationRepository;
    private final StartMenu startMenu;
    private final StateBot stateBot;
    private final String BACK = "BACK";
    private final String DELETE = "DELETE";
    private Message message;

    private Map<Integer, Violation> messagesViolation = new HashMap<>();

    public ListViolation(TelegramBotSendMessage telegramBotSendMessage, ViolationRepository violationRepository,
                         @Lazy StartMenu startMenu, StateBot stateBot) {
        this.telegramBotSendMessage = telegramBotSendMessage;
        this.violationRepository = violationRepository;
        this.startMenu = startMenu;
        this.stateBot = stateBot;
    }


    @Override
    public void doing(Update update) {
        if (update.hasCallbackQuery()) {
            sendMessageAtCallbackQuery(update);
        } else {
            sendMessageAtText(update);
        }
    }

    @Override
    public InlineKeyboardMarkup createInlineKeyboardMarkup() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> button = new ArrayList<>();
        List<InlineKeyboardButton> buttonRow1 = new ArrayList<>();

        InlineKeyboardButton back = new InlineKeyboardButton("Назад");
        back.setCallbackData(BACK);
        buttonRow1.add(back);

        button.add(buttonRow1);
        inlineKeyboardMarkup.setKeyboard(button);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup createInlineKeyboardMarkupForViolation() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> button = new ArrayList<>();
        List<InlineKeyboardButton> buttonRow1 = new ArrayList<>();

        InlineKeyboardButton delete = new InlineKeyboardButton("Удалить");
        delete.setCallbackData(DELETE);
        buttonRow1.add(delete);

        button.add(buttonRow1);
        inlineKeyboardMarkup.setKeyboard(button);
        return inlineKeyboardMarkup;
    }

    public Message sendMessageAtCallbackQuery(Update update) {
        Message newMessage = update.getCallbackQuery().getMessage();
        Long chatId = newMessage.getChatId();
        String data = update.getCallbackQuery().getData();
        switch (data) {
            case BACK -> {
                for (Integer messageId : messagesViolation.keySet()) {
                    telegramBotSendMessage.deleteMessage(chatId, messageId);
                }
                stateBot.setAction(startMenu);
                Action action = stateBot.getAction();
                InlineKeyboardMarkup inlineKeyboardMarkup = action.createInlineKeyboardMarkup();
                telegramBotSendMessage.editMessageText(chatId, message.getMessageId(), "Вы вернулись в стартовое меню");
                telegramBotSendMessage.editMessageReplyMarkup(chatId, message.getMessageId(), inlineKeyboardMarkup);

            }
            case DELETE -> {
                Integer messageId = newMessage.getMessageId();
                Violation violation = messagesViolation.get(messageId);
                violationRepository.delete(violation);
                telegramBotSendMessage.deleteMessage(chatId, messageId);

            }
        }


        return newMessage;
    }

    public Message sendListViolation(Long chatId, Subscriber subscriber, Message newMessage) {
        message = newMessage;
        List<Violation> violations;
        messagesViolation = new HashMap<>();
        if (subscriber.isAdmin()) {
            violations = violationRepository.findAll();
        } else {
            violations = violationRepository.findById(chatId).stream().toList();
        }
        if (violations.isEmpty()) {
            telegramBotSendMessage.editMessageText(chatId, message.getMessageId(), "Нарушений нет");
            telegramBotSendMessage.editMessageReplyMarkup(chatId, message.getMessageId(), createInlineKeyboardMarkup());
            return message;
        } else {
            for (Violation violation : violations) {
                String textViolation = createTextViolation(violation);
                SendPhoto sendPhoto = createSendPhoto(textViolation, chatId, violation.getId());
                sendPhoto.setReplyMarkup(createInlineKeyboardMarkupForViolation());
                Message messageViolation = telegramBotSendMessage.sendPhotoFromUser(sendPhoto);
                messagesViolation.put(messageViolation.getMessageId(), violation);
            }
            telegramBotSendMessage.editMessageText(chatId, message.getMessageId(), "Удалите уже неактуальные нарушения, а потом вернитесь в меню");
            telegramBotSendMessage.editMessageReplyMarkup(chatId, message.getMessageId(), createInlineKeyboardMarkup());
            return message;
        }
    }


//    private SendMessage createMessage(String text, Long chatId) {
//        SendMessage sendMessage = new SendMessage();
//        sendMessage.setText(text);
//        sendMessage.setChatId(chatId);
//        InlineKeyboardMarkup inlineKeyboardMarkup = stateBot.getAction().createInlineKeyboardMarkup();
//        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
//        return sendMessage;
//    }

    private String createTextViolation(Violation violation) {
        return "Нарушение: " + violation.getText()
                + "\nМесто нарушения: " + violation.getPlace();
    }

    private SendPhoto createSendPhoto(String text, Long chatId, Long violationId) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setCaption(text);
        Photo photo = violationRepository.getPhotoByViolationId(violationId);
        getSendPhotoWithPhoto(sendPhoto, photo.getPath());
        InlineKeyboardMarkup inlineKeyboardMarkup = createInlineKeyboardMarkupForViolation();
        sendPhoto.setReplyMarkup(inlineKeyboardMarkup);
        return sendPhoto;
    }

    private SendPhoto getSendPhotoWithPhoto(SendPhoto sendPhoto, String url) {
        try (InputStream stream = new URL(url).openStream()) {
            sendPhoto.setPhoto(new InputFile(stream, url));
            return sendPhoto;
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return sendPhoto;
    }

    private boolean sendMessageAtText(Update update) {
        Long chatId = update.getMessage().getChatId();
        return telegramBotSendMessage.deleteMessage(chatId, update.getMessage().getMessageId());
    }


}
