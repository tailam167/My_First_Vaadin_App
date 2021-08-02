package com.vaadin.application.service;

import com.vaadin.application.model.Product;
import com.vaadin.application.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Product Service
 *
 * @author tailam
 */
@Service
@CacheConfig(cacheNames = "productCache")
public class ProductService {
    private final ProductRepository productRepository;
    private static final Logger LOGGER = Logger.getLogger(ProductService.class.getName());

    @Autowired
    public ProductService(ProductRepository ProductRepository) {
        this.productRepository = ProductRepository;
    }

    /**
     * List and find all product in database
     *
     * @return listProduct
     */
    @Cacheable(cacheNames = "findAllProductCache", key = "'ALL'")
    public List<Product> findAllProduct() {
        return productRepository.findAll();
    }

    /**
     * Delete product
     *
     * @return product
     * @author tailam
     */
    @CacheEvict(cacheNames = "deleteProductCache", key = "#product", allEntries = true)
    public Product deleteProduct(Product product) {
        productRepository.delete(product);
        return product;
    }

    /**
     * Update product
     *
     * @author tailam
     */
    @CachePut(cacheNames = "updateProductCache", key = "#product", unless = "#product!=null")
    public Product updateProduct(Product product) {
        if (product == null) {
            LOGGER.log(Level.SEVERE,
                    "Product is null. Are you sure you have connected your form to the application?");
            return null;
        }
        return productRepository.save(product);
    }

    /**
     * Save Product
     *
     * @author tailam
     */
    public void save(Product product) {
        if (product == null) {
            LOGGER.log(Level.SEVERE,
                    "Product is null. Are you sure you have connected your form to the application?");
            return;
        }
        productRepository.save(product);
    }

    /**
     * Count product
     *
     * @return numberOfProduct
     */
    public Long count() {
        return productRepository.count();
    }

    /**
     * DashBoard show
     *
     * @return stats
     */
    public Map<String, Integer> getStats() {
        HashMap<String, Integer> stats = new HashMap<>();
        findAllProduct().forEach(product ->
                stats.put(product.getProductName(), product.getProducts().size()));
        return stats;
    }
}
