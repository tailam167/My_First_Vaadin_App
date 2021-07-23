package com.vaadin.application.repository;

import com.vaadin.application.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer > {
    @Query("select p from Product p " +
            "where lower(p.productName) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(p.productCode) like lower(concat('%', :searchTerm, '%'))" +
            "or lower(p.releaseDate) like lower(concat('%', :searchTerm, '%'))" +
            "or lower(p.price) like lower(concat('%', :searchTerm, '%'))" +
            "or lower(p.starRating) like lower(concat('%', :searchTerm, '%'))" +
            "or lower(p.imageUrl) like lower(concat('%', :searchTerm, '%'))")
    List<Product> search(@Param("searchTerm") String searchTerm);
}
