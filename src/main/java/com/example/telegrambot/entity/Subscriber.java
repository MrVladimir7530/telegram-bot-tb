package com.example.telegrambot.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class Subscriber {
    @Id
    private long chatId;
    private String name;
    private String UserName;
    private boolean admin;
}
