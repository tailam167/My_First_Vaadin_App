package com.vaadin.application.config;

import com.vaadin.application.model.Product;
import com.vaadin.application.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductCache {

    @Autowired
    ProductService productService;

    @Cacheable(value = "productCache", key = "#product")
    public List<Product> getProduct(Product product){
        System.out.println("Retrieving from database for product");
        return productService.findAllProduct();
    }
}
