package com.khpi.wanderua.dto;

import com.khpi.wanderua.enums.AdvertisementType;
import com.khpi.wanderua.enums.TourType;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreateTourAdvertRequest extends CreateAdvertisementRequest {
    @NotNull(message = "Тип туру є обов'язковим")
    private TourType tourType;

    private List<String> tourTheme = new ArrayList<>();
    @Min(value = 1, message = "Тривалість повинна бути більше 0")
    @Max(value = 1000, message = "Тривалість не може перевищувати 1000 годин")
    private Integer duration;

    @DecimalMin(value = "0.0", inclusive = false, message = "Ціна повинна бути більше 0")
    @Digits(integer = 7, fraction = 2, message = "Неправильний формат ціни")
    private BigDecimal priceUsd;

    @DecimalMin(value = "0.0", inclusive = false, message = "Ціна повинна бути більше 0")
    @Digits(integer = 9, fraction = 2, message = "Неправильний формат ціни")
    private BigDecimal priceUah;

    @Override
    public AdvertisementType getAdvertisementType() {
        return AdvertisementType.TOUR;
    }
}
