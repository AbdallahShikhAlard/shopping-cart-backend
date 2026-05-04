package com.shoppingCartBackend.shoppingCartBackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity //Marks this class as a database table (Category)
public class Category {
    @Id //ID will be generated automatically
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    //One category can have many products
    @OneToMany(mappedBy = "category")
    @JsonIgnore
    private List<Product> products;


    public Category(String name) {
        this.name = name;
    }
}
