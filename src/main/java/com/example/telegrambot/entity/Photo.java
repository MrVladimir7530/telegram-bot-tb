package com.example.telegrambot.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private byte[] data;
    @ManyToOne
    @JoinColumn(name = "violation_id")
    private Violation violation;
}
