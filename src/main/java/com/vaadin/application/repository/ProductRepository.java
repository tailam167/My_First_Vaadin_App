package com.vaadin.application.repository;

import com.vaadin.application.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Product Repository
 *
 * @author tailam
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Integer > {
}
