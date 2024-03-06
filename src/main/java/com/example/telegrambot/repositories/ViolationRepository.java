package com.example.telegrambot.repositories;

import com.example.telegrambot.entity.Violation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ViolationRepository extends JpaRepository<Violation, Long> {
}
