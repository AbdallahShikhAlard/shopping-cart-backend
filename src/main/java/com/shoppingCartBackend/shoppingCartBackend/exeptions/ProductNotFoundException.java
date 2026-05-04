package com.shoppingCartBackend.shoppingCartBackend.exeptions;

public class ProductNotFoundException extends RuntimeException{
    public ProductNotFoundException(String message) {
        super(message);
    }
}
