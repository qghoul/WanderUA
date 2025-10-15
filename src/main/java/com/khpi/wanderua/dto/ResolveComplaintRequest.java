package com.khpi.wanderua.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResolveComplaintRequest {
    private boolean confirmed;
    private String adminComment;
}
