package com.vaadin.application.config;

import com.vaadin.application.model.Product;
import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logging cache
 *
 * @author tailam
 */
public class CacheLogger implements CacheEventListener<Product, Product> {

    private final Logger LOG = LoggerFactory.getLogger(CacheLogger.class);

    @Override
    public void onEvent(CacheEvent<? extends Product , ? extends Product> cacheEvent) {
        LOG.info("Key: {} | EventType: {} | Old value: {} | New value: {}",
                cacheEvent.getKey(), cacheEvent.getType(),
                cacheEvent.getOldValue(), cacheEvent.getNewValue());
    }
}
