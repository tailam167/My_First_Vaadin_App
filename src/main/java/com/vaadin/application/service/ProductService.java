package com.vaadin.application.service;

import com.vaadin.application.error.ProductNotFoundException;
import com.vaadin.application.model.Product;
import com.vaadin.application.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private static final Logger LOGGER = Logger.getLogger(ProductService.class.getName());

    @Autowired
    public ProductService(ProductRepository ProductRepository) {
        this.productRepository = ProductRepository;
    }

    public List<Product> findAllProduct() {
        return productRepository.findAll();
    }

    public Product findProductById(Integer productId) {
        return productRepository.findById(productId).orElseThrow(() ->
                new ProductNotFoundException("Product with id " + productId + " was not found"));
    }

    public void deleteProduct(Product product) {
        productRepository.delete(product);
    }

    public void save(Product product) {
        if (product == null) {
            LOGGER.log(Level.SEVERE,
                    "Contact is null. Are you sure you have connected your form to the application?");
            return;
        }
        productRepository.save(product);
    }

    public List<Product> findFilterProduct(String filterText) {
        if(filterText == null || filterText.isEmpty()) {
            return productRepository.findAll();
        } else  {
            return  productRepository.search(filterText);
        }
    }

    public Long count() {
        return productRepository.count();
    }

    public Map<String, Integer> getStats() {
        HashMap<String, Integer> stats = new HashMap<>();
        findAllProduct().forEach(product ->
                stats.put(product.getProductName(), product.getProducts().size()));
        return stats;
    }
}
