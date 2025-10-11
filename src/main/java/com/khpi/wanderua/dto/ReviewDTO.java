package com.khpi.wanderua.dto;

import com.khpi.wanderua.enums.GoWithOptions;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ReviewDTO {
    private Long id;

    @NotNull(message = "Рейтинг є обов'язковим")
    @Min(value = 1, message = "Мінімальний рейтинг - 1")
    @Max(value = 5, message = "Максимальний рейтинг - 5")
    private Integer rating;

    @Size(max = 1000, message = "Коментар не може бути довшим за 1000 символів")
    private String comment;

    @Size(max = 255, message = "Заголовок не може бути довшим за 255 символів")
    private String title;

    private GoWithOptions goWith;

    private LocalDate date;
    private String username;
    private Integer usefulScore;
    private List<String> imageUrls;

    // For creating new review
    @NotNull(message = "ID оголошення є обов'язковим")
    private Long advertisementId;

    private Boolean markedAsUsefulByCurrentUser;

    // Display helper methods
    public String getGoWithDisplay() {
        if (goWith == null) return null;
        return switch (goWith) {
            case SOLO -> "Один/одна";
            case COUPLE -> "Пара";
            case FAMILY -> "Сім'я";
            case FRIENDS -> "Друзі";
            case BUSINESS -> "Бізнес";
            case OTHER -> "Інше";
        };
    }

    public String getDateDisplay() {
        if (date == null) return null;
        return switch (date.getMonth()) {
            case JANUARY -> "Січень " + date.getYear();
            case FEBRUARY -> "Лютий " + date.getYear();
            case MARCH -> "Березень " + date.getYear();
            case APRIL -> "Квітень " + date.getYear();
            case MAY -> "Травень " + date.getYear();
            case JUNE -> "Червень " + date.getYear();
            case JULY -> "Липень " + date.getYear();
            case AUGUST -> "Серпень " + date.getYear();
            case SEPTEMBER -> "Вересень " + date.getYear();
            case OCTOBER -> "Жовтень " + date.getYear();
            case NOVEMBER -> "Листопад " + date.getYear();
            case DECEMBER -> "Грудень " + date.getYear();
        };
    }
}