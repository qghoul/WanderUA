package com.khpi.wanderua.dto;

import com.khpi.wanderua.enums.AdvertisementType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreatePublicAttractionAdvertRequest extends CreateAdvertisementRequest {
    @NotBlank(message = "Адреса є обов'язковою")
    private String address;
    private Boolean freeVisit;
    @Override
    public AdvertisementType getAdvertisementType() {
        return AdvertisementType.PUBLIC_ATTRACTION;
    }
}