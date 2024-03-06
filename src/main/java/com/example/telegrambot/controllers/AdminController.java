package com.example.telegrambot.controllers;

import com.example.telegrambot.entity.Subscriber;
import com.example.telegrambot.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AdminController {
    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

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
