package com.khpi.wanderua.dto;

import com.khpi.wanderua.enums.ComplaintType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintRequest {

    @NotNull(message = "Тип скарги обов'язковий")
    private ComplaintType type;

    private String comment;
}