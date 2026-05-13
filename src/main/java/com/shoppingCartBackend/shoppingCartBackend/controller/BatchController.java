package com.shoppingCartBackend.shoppingCartBackend.controller;

import com.shoppingCartBackend.shoppingCartBackend.dto.BatchResultResponse;
import com.shoppingCartBackend.shoppingCartBackend.service.order.IOrderService;
import com.shoppingCartBackend.shoppingCartBackend.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BatchController {

    private final OrderService orderService;


    @GetMapping("/daily-sales/before")
    public BatchResultResponse before() {

        return orderService.processDailySalesWithoutChunking();
    }

    @GetMapping("/daily-sales/after")
    public BatchResultResponse after() {

        return orderService.processDailySalesWithChunking();
    }
}