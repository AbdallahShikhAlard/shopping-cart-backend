package com.shoppingCartBackend.shoppingCartBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class BatchResultResponse {

    private int totalOrders;

    private BigDecimal totalSales;

    private long executionTime;

    private String processingType;
}