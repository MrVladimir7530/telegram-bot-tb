package com.example.telegrambot.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String path;
    private String mediaType;
    private Long size;
    @ManyToOne
    @JoinColumn(name = "violation_id")
    private Violation violation;
}
