package com.shoppingCartBackend.shoppingCartBackend.controller;

import com.shoppingCartBackend.shoppingCartBackend.response.ApiResponse;
import com.shoppingCartBackend.shoppingCartBackend.service.order.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
public class AcidTestController {

    private final IOrderService orderService;

    // ── Normal order (no failure) ──────────────────────────────
    @PostMapping("/order/normal/{userId}")
    public ResponseEntity<ApiResponse> normalOrder(
            @PathVariable Long userId) {
        System.clearProperty("test.simulate.failure");
        try {
            var order = orderService.placeOrder(userId);
            return ResponseEntity.ok(
                    new ApiResponse("Order placed successfully", order));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    // ── Simulate failure mid-transaction ──────────────────────
    @PostMapping("/order/simulate-failure/{userId}")
    public ResponseEntity<ApiResponse> failureOrder(
            @PathVariable Long userId) {
        System.setProperty("test.simulate.failure", "true");
        try {
            var order = orderService.placeOrder(userId);
            return ResponseEntity.ok(
                    new ApiResponse("Order placed", order));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse(
                            "Transaction failed — rollback triggered: "
                                    + e.getMessage(), null));
        } finally {
            System.clearProperty("test.simulate.failure");
        }
    }
}