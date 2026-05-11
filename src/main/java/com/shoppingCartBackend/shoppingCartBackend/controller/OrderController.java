package com.shoppingCartBackend.shoppingCartBackend.controller;

import com.shoppingCartBackend.shoppingCartBackend.dto.OrderDto;
import com.shoppingCartBackend.shoppingCartBackend.exeptions.ResourceNotFoundException;
import com.shoppingCartBackend.shoppingCartBackend.model.Order;
import com.shoppingCartBackend.shoppingCartBackend.response.ApiResponse;
import com.shoppingCartBackend.shoppingCartBackend.service.notification.NotificationService;
import com.shoppingCartBackend.shoppingCartBackend.service.order.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/orders")
public class OrderController {
    private final IOrderService orderService;
    private final NotificationService notificationService;

    @PostMapping("/order")
    public ResponseEntity<ApiResponse> createOrder(@RequestParam Long userId) {
        Order order = orderService.placeOrder(userId); // العملية الأساسية (قاعدة البيانات)

        notificationService.sendOrderConfirmation(order.getOrderId());

        return ResponseEntity.ok(new ApiResponse("Success", orderService.convertToDto(order)));
    }

    @GetMapping("/{orderId}/order")
    public ResponseEntity<ApiResponse> getOrderById(@PathVariable Long orderId) {
        try {
            OrderDto order = orderService.getOrderById(orderId);
            return ResponseEntity.ok(new ApiResponse("Order fetched successfully", order));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("Error when fetching order", e.getMessage()));
        }
    }

    @GetMapping("/{userId}/order")
    public ResponseEntity<ApiResponse> getUserOrders(@PathVariable Long userId) {
        try {
            List<OrderDto> order = orderService.getUserOrders(userId);
            return ResponseEntity.ok(new ApiResponse("Users orders fetched successfully", order));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("Error when fetching user orders", e.getMessage()));
        }
    }

}
