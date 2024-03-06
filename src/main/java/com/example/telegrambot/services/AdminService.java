package com.example.telegrambot.services;

import com.example.telegrambot.entity.Subscriber;
import com.example.telegrambot.repositories.SubscriberRepository;
import lombok.RequiredArgsConstructor;
import org.jvnet.hk2.annotations.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final SubscriberRepository subscriberRepository;
    public Subscriber add(Long chat_id) {
        Subscriber subscriber = new Subscriber();
        subscriber.setAdmin(true);
        subscriber.setChatId(chat_id);

        return subscriberRepository.save(subscriber);
    }

    public boolean delete(Long chat_id) {
        try {
            subscriberRepository.deleteById(chat_id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<Subscriber> getAll() {
        return subscriberRepository.findAll();
    }
}
