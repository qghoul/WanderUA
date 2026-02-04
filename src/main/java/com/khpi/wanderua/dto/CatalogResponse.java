package com.khpi.wanderua.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
