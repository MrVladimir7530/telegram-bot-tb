package com.example.telegrambot.repositories;

import com.example.telegrambot.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
}
