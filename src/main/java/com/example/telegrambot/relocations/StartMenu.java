package com.example.telegrambot.relocations;

import com.example.telegrambot.component.TelegramBotSendMessage;
import com.example.telegrambot.entity.Subscriber;
import com.example.telegrambot.repositories.SubscriberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
@Primary
@Slf4j
public class StartMenu implements Action {
    private final TelegramBotSendMessage telegramBotSendMessage;
    private final DescriptionViolation descriptionViolation;
    private final ListViolation listViolation;

    private final SubscriberRepository subscriberRepository;
    private StateBot stateBot;
    private final String VIOLATION = "VIOLATION";
    private final String LIST = "LIST";
    private Message message;

    public StartMenu(TelegramBotSendMessage telegramBotSendMessage, ListViolation listViolation,
                     SubscriberRepository subscriberRepository, DescriptionViolation descriptionViolation,
                     @Lazy StateBot stateBot) {
        this.telegramBotSendMessage = telegramBotSendMessage;
        this.listViolation = listViolation;
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
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        String data = update.getCallbackQuery().getData();
        switch (data) {
            case VIOLATION -> {
                stateBot.setAction(descriptionViolation);
                telegramBotSendMessage.editMessageText(chatId, message.getMessageId(), "Опишите нарушение");
//                SendMessage sendMessage = createMessage("Опишите нарушение", chatId);
//                message = telegramBotSendMessage.sendMessageFromUser(sendMessage);
                return message;
            }

            case LIST -> {
                Subscriber subscriber = subscriberRepository.findById(chatId).get();
                stateBot.setAction(listViolation);
                listViolation.sendListViolation(chatId, subscriber, message);

                //todo дописат получение всех

            }
        }
        return message;
    }



    public Message sendMessageAtText(Update update) {
        Long chatId = update.getMessage().getChatId();
        String text = "Выберите действие";
        if (message == null) {
            saveNewSubscriber(update);
            SendMessage sendMessage = createMessage(text, chatId);
            sendMessage.setReplyMarkup(createInlineKeyboardMarkup());
            message = telegramBotSendMessage.sendMessageFromUser(sendMessage);
            return message;
        }
        telegramBotSendMessage.deleteMessage(chatId, update.getMessage().getMessageId());
        return message;
    }


    public SendMessage createMessage(String text, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(text);
        sendMessage.setChatId(chatId);
        return sendMessage;
    }



    public Subscriber saveNewSubscriber(Update update) {
        Subscriber subscriber = new Subscriber();
        subscriber.setChatId(update.getMessage().getChatId());
        subscriber.setName(update.getMessage().getFrom().getFirstName());
        subscriber.setUserName(update.getMessage().getFrom().getUserName());
        return subscriberRepository.save(subscriber);
    }


}
