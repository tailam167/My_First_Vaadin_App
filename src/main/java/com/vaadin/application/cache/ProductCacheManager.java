package com.vaadin.application.cache;

import com.vaadin.application.model.Product;
import com.vaadin.application.service.ProductService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;

/**
 * Configuration cache
 *
 * @author tailam
 */
@Configuration
@EnableCaching
public class ProductCacheManager {

    private static Logger logger = LogManager.getLogger(ProductCacheManager.class);
    private final ProductService productService;
    private Product product;

    public ProductCacheManager(ProductService productService) {
        this.productService = productService;
    }

    public Cache<String, List> findAllProductCache() {
        // Find all product in cache
        CacheManager findAllProductCacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .withCache("findAllProductCache",
                        CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, List.class,
                                ResourcePoolsBuilder.heap(10))
                                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(10)))).build();
        findAllProductCacheManager.init();
        Cache<String, List> findAllProductCache = findAllProductCacheManager
                .getCache("findAllProductCache", String.class, List.class);
        findAllProductCache.put("'ALL'", productService.findAllProduct());
        logger.info("Retrieved data from database into cache with key: ALL");
        return findAllProductCache;
    }

    public Cache<String, Product> updateProductCache(Product product) {
        // Update product in cache
        CacheManager updateProductCacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .withCache("updateProductCache",
                        CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, Product.class,
                                ResourcePoolsBuilder.heap(10))
                                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(10)))).build();
        updateProductCacheManager.init();
        Cache<String, Product> updateProductCache = updateProductCacheManager
                .getCache("updateProductCache", String.class, Product.class);
        updateProductCache.put("'UPDATED'", productService.updateProduct(product));
        logger.info("Update data in cache with key: UPDATED");
        return updateProductCache;
    }

    public Cache<String, Product> deleteProductCache(Product product) {
        // Delete product in cache
        CacheManager deleteProductCacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .withCache("deleteProductCache",
                        CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, Product.class,
                                ResourcePoolsBuilder.heap(10))
                                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(10)))).build();
        deleteProductCacheManager.init();
        Cache<String, Product> deleteProductCache = deleteProductCacheManager
                .getCache("deleteProductCache", String.class, Product.class);
        deleteProductCache.put("'DELETED'", productService.deleteProduct(product));
        logger.info("Deleted data in cache with key: DELETED");
        return deleteProductCache;
    }
}
