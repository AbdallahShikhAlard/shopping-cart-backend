package com.shoppingCartBackend.shoppingCartBackend.controller;

import com.shoppingCartBackend.shoppingCartBackend.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BatchController {

    private final OrderService orderService;

    // =========================================
    // WITHOUT CHUNKING
    // =========================================

    @GetMapping("/batch/daily-sales/before")
    public String before() {

        return orderService.processDailySalesWithoutChunking();
    }

    // =========================================
    // WITH CHUNKING
    // =========================================

    @GetMapping("/batch/daily-sales/after")
    public String after() {

        orderService.processDailySalesWithChunking();

        return "Daily Sales Batch Job Started WITH Chunking...";
    }
}