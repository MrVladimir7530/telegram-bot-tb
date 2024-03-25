package com.example.telegrambot.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Violation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String text;
    private String place;
    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Subscriber subscriber;
}
