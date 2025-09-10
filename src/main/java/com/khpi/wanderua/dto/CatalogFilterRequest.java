package com.khpi.wanderua.dto;

import com.khpi.wanderua.enums.AdvertisementType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CatalogFilterRequest {
    private String city;
    private AdvertisementType category;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String sortBy = "popular"; // popular, rating, newest, price
    private Boolean sustainableOnly = false;
    private int page = 0;
    private int size = 10;

    public CatalogFilterRequest() {}

    public CatalogFilterRequest(String city, AdvertisementType category, String sortBy) {
        this.city = city;
        this.category = category;
        this.sortBy = sortBy != null ? sortBy : "popular";
    }
}