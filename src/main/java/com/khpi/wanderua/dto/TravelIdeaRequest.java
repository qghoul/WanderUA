package com.khpi.wanderua.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class TravelIdeaRequest {
    String title;
    String description;
}
