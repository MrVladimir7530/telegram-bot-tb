package com.example.telegrambot.controllers;

import com.example.telegrambot.entity.Subscriber;
import com.example.telegrambot.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @PostMapping("/{id}")
    public ResponseEntity<Subscriber> add(@PathVariable Long id) {
        Subscriber subscriber = adminService.add(id);
        return ResponseEntity.ok(subscriber);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean result = adminService.delete(id);
        if (result) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping
    public ResponseEntity<List<Subscriber>> getALl() {
        List<Subscriber> all = adminService.getAll();
        return ResponseEntity.ok(all);
    }
}
