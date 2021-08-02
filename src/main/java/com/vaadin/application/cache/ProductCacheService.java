package com.vaadin.application.cache;

import com.vaadin.application.model.Product;
import com.vaadin.application.service.ProductService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Cache for product
 *
 * @author tailam
 */
@Service
public class ProductCacheService {

    private final ProductCacheManager productCacheManager;

    public ProductCacheService(ProductCacheManager productCacheManager, ProductService productService) {
        this.productCacheManager = productCacheManager;
    }

    @Scheduled(fixedRate = 5000)
    public List<Product> findAllProductCache(){
        return productCacheManager.findAllProductCache().get("'ALL'");
    }

    @Scheduled(fixedRate = 3000)
    public Product updateProductCache(Product product){
        return productCacheManager.updateProductCache(product).get("'UPDATED'");
    }

    @Scheduled(fixedRate = 1000)
    public Product deleteProductCache(Product product){
        return productCacheManager.deleteProductCache(product).get("'DELETED'");
    }
}
