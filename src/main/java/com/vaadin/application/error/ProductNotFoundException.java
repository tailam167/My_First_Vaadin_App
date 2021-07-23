package com.vaadin.application.error;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String messageString) {
        super(messageString);
    }
}
