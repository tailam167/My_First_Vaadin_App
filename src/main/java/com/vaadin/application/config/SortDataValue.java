package com.vaadin.application.config;

import com.vaadin.application.model.Product;

import java.util.Comparator;

/**
 * Sorting data in dataprovider
 *
 * @author tailam
 */
public class SortDataValue implements Comparator<Product> {

    @Override
    public int compare(Product o1, Product o2) {
        return (o1.getProductId().compareTo(o2.getProductId()));
    }
}
