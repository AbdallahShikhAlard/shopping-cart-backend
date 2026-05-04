package com.shoppingCartBackend.shoppingCartBackend.dto;

import com.shoppingCartBackend.shoppingCartBackend.enums.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDto {
    private Long id;
    private Long orderId;
    private LocalDateTime orderDate;
    private BigDecimal totalPrice;
    private OrderStatus status;
    private List<OrderItemDto> items;

}
