package com.example.telegrambot.repositories;

import com.example.telegrambot.entity.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {

}
