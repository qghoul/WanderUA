package com.khpi.wanderua.dto;

import com.khpi.wanderua.enums.AdvertisementType;
import com.khpi.wanderua.enums.PriceCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreateRestaurantAdvertRequest extends CreateAdvertisementRequest {
    @NotBlank(message = "Адреса є обов'язковою")
    private String address;
    @NotNull(message = "Цінова категорія є обов'язковою")
    private PriceCategory priceCategory;
    private List<String> specialOptions = new ArrayList<>();

    @Override
    public AdvertisementType getAdvertisementType() {
        return AdvertisementType.RESTAURANT;
    }
}