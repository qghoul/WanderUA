package com.khpi.wanderua.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.khpi.wanderua.enums.AdvertisementType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "advertisementType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CreateRestaurantAdvertRequest.class, name = "RESTAURANT"),
        @JsonSubTypes.Type(value = CreateTourAdvertRequest.class, name = "TOUR"),
        @JsonSubTypes.Type(value = CreateAccomodationAdvertRequest.class, name = "ACCOMODATION"),
        @JsonSubTypes.Type(value = CreateEntertainmentAdvertRequest.class, name = "ENTERTAINMENT"),
        @JsonSubTypes.Type(value = CreatePublicAttractionAdvertRequest.class, name = "PUBLIC_ATTRACTION")
})
public abstract class CreateAdvertisementRequest {
    @NotBlank(message = "Назва є обов'язковою")
    @Size(max = 255, message = "Назва не може перевищувати 255 символів")
    private String title;
    //@NotBlank(message = "Категорія є обов'язковою")
    //private String advertisementType;
    private String website;
    private String contacts;
    @Size(max = 1000, message = "Опис не може перевищувати 1000 символів")
    private String description;
    private String workdays;
    private String weekends;
    public abstract AdvertisementType getAdvertisementType();
}
