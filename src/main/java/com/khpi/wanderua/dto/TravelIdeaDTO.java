package com.khpi.wanderua.dto;

import com.khpi.wanderua.entity.Advertisement;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class TravelIdeaDTO {
    Long id;
    String title;
    String description;
    Integer advertisementCount;
    List<CatalogAdvertisementResponse> savedAdvertisements;
}
