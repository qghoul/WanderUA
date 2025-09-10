package com.khpi.wanderua.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CatalogResponse {
    private List<CatalogAdvertisementResponse> advertisements;
    private String selectedCity;
    private BigDecimal maxPriceInCatalog;
    private int totalElements;
    private int totalPages;
    private int currentPage;
    private boolean hasNext;
    private boolean hasPrevious;

    private long restaurantCount;
    private long tourCount;
    private long accommodationCount;
    private long entertainmentCount;
    private long attractionCount;
}
