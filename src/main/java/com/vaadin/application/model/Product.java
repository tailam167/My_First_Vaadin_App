package com.vaadin.application.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Entity Product
 *
 * @author tailam
 */
@Entity
@Table(name = "product")
public class Product implements Serializable {

    @Id
    @Column(name = "product_id")
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "productId")
    @SequenceGenerator(name = "productId", sequenceName = "next_val")
    private Integer productId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_code")
    private String productCode;

    @Column(name = "release_date")
    private Date releaseDate;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private Float price;

    @Column(name = "star_rating")
    private Float starRating;

    @Column(name = "image_url")
    private String imageUrl;

    public Product(Integer productId, String productName, String productCode,
                   Date releaseDate, String description, Float price,
                   Float starRating, String imageUrl) {
        this.productId = productId;
        this.productName = productName;
        this.productCode = productCode;
        this.releaseDate = releaseDate;
        this.description = description;
        this.price = price;
        this.starRating = starRating;
        this.imageUrl = imageUrl;
    }

    public Product() {

    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public boolean isPersisted() {
        return productId != null;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Float getStarRating() {
        return starRating;
    }

    public void setStarRating(Float starRating) {
        this.starRating = starRating;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<Product> getProducts() {
        return new LinkedList<>();
    }
}
