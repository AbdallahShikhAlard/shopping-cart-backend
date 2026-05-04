package com.shoppingCartBackend.shoppingCartBackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //primary key
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;

    @ManyToOne //Many cart Items to one product
    @JoinColumn(name = "product_id") //Cart item has the product_id, FK
    private Product product;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL) //many cartItems to one cart
    @JoinColumn(name = "cart_id") //Cart item has the cart_id, FK
    private Cart cart;

    public void setTotalPrice() {
        this.totalPrice = this.unitPrice.multiply(new BigDecimal(quantity));
    }

}
