package com.khpi.wanderua.dto;

import com.khpi.wanderua.enums.AccomodationType;
import com.khpi.wanderua.enums.AdvertisementType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreateAccomodationAdvertRequest extends CreateAdvertisementRequest {
    @NotBlank(message = "Адреса є обов'язковою")
    private String address;

    @NotNull(message = "Тип житла є обов'язковим")
    private AccomodationType accomodationType; // название соответствует frontend

    @Override
    public AdvertisementType getAdvertisementType() {
        return AdvertisementType.ACCOMODATION;
    }
}
