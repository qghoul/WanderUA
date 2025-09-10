package com.khpi.wanderua.dto;

import com.khpi.wanderua.enums.AdvertisementType;
import com.khpi.wanderua.enums.AgeCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreateEntertainmentAdvertRequest extends CreateAdvertisementRequest {
    @NotBlank(message = "Адреса є обов'язковою")
    private String address;
    @NotNull(message = "Вікова категорія є обов'язковою")
    private AgeCategory ageCategory;
    @Override
    public AdvertisementType getAdvertisementType() {
        return AdvertisementType.ENTERTAINMENT;
    }
}