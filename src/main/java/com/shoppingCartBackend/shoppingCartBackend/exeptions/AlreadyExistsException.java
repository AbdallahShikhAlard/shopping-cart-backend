package com.shoppingCartBackend.shoppingCartBackend.exeptions;

public class AlreadyExistsException extends RuntimeException {
    public AlreadyExistsException(String s) {
        super(s);
    }
}
