package com.shoppingCartBackend.shoppingCartBackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/api/public/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Instance running on port: " +
                System.getProperty("server.port", "unknown"));
    }
}