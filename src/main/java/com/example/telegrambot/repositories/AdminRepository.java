package com.example.telegrambot.repositories;

import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class AdminRepository {
    private HashSet<Integer> admins = new HashSet<>();

    public boolean addChatId(Integer chatId) {
        return admins.add(chatId);
    }

    public HashSet<Integer> getAdmins() {
        return admins;
    }
}
