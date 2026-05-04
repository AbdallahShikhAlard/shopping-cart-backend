package com.shoppingCartBackend.shoppingCartBackend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity // This class represents a table in the database
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String brand;

    private BigDecimal price;
    private int inventory; //Stock quantity
    private String description;

    //Many products can belong to one Category
    @ManyToOne() //Many products to one category,
    @JoinColumn(name = "category_id") //this creates the foreign key in the product table.
    @JsonBackReference
    private Category category;

    // One product can have many images
    //If a product is deleted, its associated images are deleted too (orphanRemoval = true).
    @JsonBackReference
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images;

    public Product(String name, String brand, BigDecimal price, int inventory, String description, Category category) {
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.inventory = inventory;
        this.description = description;
        this.category = category;
    }
}
