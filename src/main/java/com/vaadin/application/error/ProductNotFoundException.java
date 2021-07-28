package com.vaadin.application.error;

/**
 * Exception for product not found
 *
 * @author tailam
 */
public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String messageString) {
        super(messageString);
    }
}
