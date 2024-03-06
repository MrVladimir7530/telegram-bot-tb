package com.example.telegrambot.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/start")
public class TestController {

    @GetMapping
    public String getStart() {
        return "Hello world";
    }
}
