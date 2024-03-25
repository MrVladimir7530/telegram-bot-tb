package com.example.telegrambot.relocations;

import com.example.telegrambot.entity.Subscriber;
import com.example.telegrambot.entity.Violation;
import com.example.telegrambot.repositories.SubscriberRepository;
import com.example.telegrambot.repositories.ViolationRepository;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Primary
public class StartMenu implements Action {
    private final ViolationRepository violationRepository;
    private final SubscriberRepository subscriberRepository;
    private final DescriptionViolation descriptionViolation;
    private StateBot stateBot;
    private final String VIOLATION = "VIOLATION";
    private final String LIST = "LIST";

    public StartMenu(ViolationRepository violationRepository, SubscriberRepository subscriberRepository, DescriptionViolation descriptionViolation,@Lazy StateBot stateBot) {
        this.violationRepository = violationRepository;
        this.subscriberRepository = subscriberRepository;
        this.descriptionViolation = descriptionViolation;
        this.stateBot = stateBot;
    }


    @Override
    public SendMessage doing(Update update) {
        SendMessage sendMessage = new SendMessage();
        if (update.hasCallbackQuery()) {
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            sendMessage.setChatId(chatId);
            return getMessageAtCallbackQuery(update, sendMessage);

        } else {
            Long chatId = update.getMessage().getChatId();
            if (subscriberRepository.findById(chatId).isEmpty()) {
                getNewSubscriber(update);
            }
            sendMessage.setChatId(chatId);
            sendMessage.setText("Hi, vova");
            return sendMessage;
        }
    }

    public InlineKeyboardMarkup createInlineKeyboardMarkup() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton addedViolation = new InlineKeyboardButton("Добавить нарушение");
        InlineKeyboardButton addedList = new InlineKeyboardButton("Список нарушений");

        addedViolation.setCallbackData(VIOLATION);
        addedList.setCallbackData(LIST);

        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(addedViolation);
        buttons.add(addedList);

        inlineKeyboardMarkup.setKeyboard(Collections.singletonList(buttons));
        return inlineKeyboardMarkup;
    }

    public SendMessage getMessageAtCallbackQuery(Update update, SendMessage sendMessage) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Subscriber subscriber = subscriberRepository.findById(chatId).get();
        String data = update.getCallbackQuery().getData();
        switch (data) {
            case VIOLATION -> {
                sendMessage.setText("Опишите проблему");
                stateBot.setAction(descriptionViolation);
                return sendMessage;
            }

            case LIST -> {
                sendMessage.setText("Список ваших нарушений");

                if (subscriber.isAdmin()) {
                    List<Violation> all = violationRepository.findAll();

                }
                List<Violation> allById = violationRepository.findById(chatId).stream().toList();
            }
        }
        //todo дописать return
        return null;
    }

    public Subscriber getNewSubscriber(Update update) {
        Subscriber subscriber = new Subscriber();
        subscriber.setChatId(update.getMessage().getChatId());
        subscriber.setName(update.getMessage().getFrom().getFirstName());
        subscriber.setUserName(update.getMessage().getFrom().getUserName());
        return subscriberRepository.save(subscriber);
    }


}
