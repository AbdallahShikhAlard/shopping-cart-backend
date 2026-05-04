package com.shoppingCartBackend.shoppingCartBackend.dto;

import com.shoppingCartBackend.shoppingCartBackend.model.Category;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductDto {
    private Long id;
    private String name;
    private String brand;

    private BigDecimal price;
    private int inventory; //Stock quantity
    private String description;
    private Category category;
    private List<ImageDto> images;
}
