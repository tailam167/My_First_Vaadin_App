package com.vaadin.application.config;

import com.vaadin.application.model.Product;
import com.vaadin.application.service.ProductService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Cache for product
 *
 * @author tailam
 */
@Component
public class ProductCache {

    ProductService productService;

    @Cacheable(value = "products", key = "#product")
    public List<Product> getProducts() {
        System.out.println("Retrieving from database for product list: " + productService.findAllProduct());
        return productService.findAllProduct();
    }
}
