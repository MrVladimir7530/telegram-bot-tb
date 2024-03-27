package com.example.telegrambot.relocations;

import com.example.telegrambot.component.TelegramBotSendMessage;
import com.example.telegrambot.entity.Photo;
import com.example.telegrambot.entity.Subscriber;
import com.example.telegrambot.entity.Violation;
import com.example.telegrambot.repositories.SubscriberRepository;
import com.example.telegrambot.repositories.ViolationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
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
import java.util.List;

@Service
@Primary
@Slf4j
public class StartMenu implements Action {
    private final TelegramBotSendMessage telegramBotSendMessage;

    private final ViolationRepository violationRepository;
    private final SubscriberRepository subscriberRepository;
    private final DescriptionViolation descriptionViolation;
    private StateBot stateBot;
    private final String VIOLATION = "VIOLATION";
    private final String LIST = "LIST";

    public StartMenu(TelegramBotSendMessage telegramBotSendMessage, ViolationRepository violationRepository,
                     SubscriberRepository subscriberRepository, DescriptionViolation descriptionViolation,
                     @Lazy StateBot stateBot) {
        this.telegramBotSendMessage = telegramBotSendMessage;
        this.violationRepository = violationRepository;
        this.subscriberRepository = subscriberRepository;
        this.descriptionViolation = descriptionViolation;
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
        List<InlineKeyboardButton> buttonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> buttonsRow2 = new ArrayList<>();

        InlineKeyboardButton addedViolation = new InlineKeyboardButton("Добавить нарушение");
        addedViolation.setCallbackData(VIOLATION);
        buttonsRow1.add(addedViolation);

        InlineKeyboardButton listViolation = new InlineKeyboardButton("Список нарушений");
        listViolation.setCallbackData(LIST);
        buttonsRow2.add(listViolation);

        button.add(buttonsRow1);
        button.add(buttonsRow2);
        inlineKeyboardMarkup.setKeyboard(button);
        return inlineKeyboardMarkup;
    }


    public Message sendMessageAtCallbackQuery(Update update) {
        Message message = new Message();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        String data = update.getCallbackQuery().getData();
        switch (data) {
            case VIOLATION -> {
                stateBot.setAction(descriptionViolation);
                SendMessage sendMessage = createMessage("Опишите нарушение", chatId);
                message = telegramBotSendMessage.sendMessageFromUser(sendMessage);
                return message;
            }

            case LIST -> {
                Subscriber subscriber = subscriberRepository.findById(chatId).get();
                SendMessage sendMessage = createMessage("Список ваших нарушений", chatId);
                message = telegramBotSendMessage.sendMessageFromUser(sendMessage);

                List<Violation> violations;
                if (subscriber.isAdmin()) {
                    violations = violationRepository.findAll();
                } else {
                    violations = violationRepository.findById(chatId).stream().toList();
                }
                for (Violation violation : violations) {
                    String textViolation = createTextViolation(violation);
                    SendPhoto sendPhoto = createSendPhoto(textViolation, chatId, violation.getId());
                    telegramBotSendMessage.sendPhotoFromUser(sendPhoto);
                }
            }
        }
        return message;
    }

    public String createTextViolation(Violation violation) {
        return "Нарушение: " + violation.getText()
                + "\nМесто нарушения: " + violation.getPlace();
    }

    public Message sendMessageAtText(Update update) {
        Long chatId = update.getMessage().getChatId();
        if (subscriberRepository.findById(chatId).isEmpty()) {
            getNewSubscriber(update);
        }
        SendMessage sendMessage = createMessage("Выберите действие", chatId);
        return telegramBotSendMessage.sendMessageFromUser(sendMessage);
    }


    public SendMessage createMessage(String text, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(text);
        sendMessage.setChatId(chatId);
        InlineKeyboardMarkup inlineKeyboardMarkup = stateBot.getAction().createInlineKeyboardMarkup();
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }

    public SendPhoto createSendPhoto(String text, Long chatId, Long violationId ) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setCaption(text);
        Photo photo = violationRepository.getPhotoByViolationId(violationId);
        getSendPhotoWithPhoto(sendPhoto, photo.getPath());
        //todo поменять на норм клавиатуру
        InlineKeyboardMarkup inlineKeyboardMarkup = stateBot.getAction().createInlineKeyboardMarkup();
        sendPhoto.setReplyMarkup(inlineKeyboardMarkup);
        return sendPhoto;
    }

    public SendPhoto getSendPhotoWithPhoto(SendPhoto sendPhoto, String url) {
        try (InputStream stream = new URL(url).openStream()) {
            sendPhoto.setPhoto(new InputFile(stream, url));
            return sendPhoto;
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return sendPhoto;
    }


    public Subscriber getNewSubscriber(Update update) {
        Subscriber subscriber = new Subscriber();
        subscriber.setChatId(update.getMessage().getChatId());
        subscriber.setName(update.getMessage().getFrom().getFirstName());
        subscriber.setUserName(update.getMessage().getFrom().getUserName());
        return subscriberRepository.save(subscriber);
    }


}
