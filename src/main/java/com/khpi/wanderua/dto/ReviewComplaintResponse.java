package com.khpi.wanderua.dto;

import com.khpi.wanderua.enums.ComplaintType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewComplaintResponse {

    private Long id;
    private Long userId;
    private String username;
    private Long reviewId;
    private String reviewTitle;
    private String reviewText;
    private List<String> reviewImageUrls;
    private Long advertisementId;
    private String advertisementTitle;
    private ComplaintType type;
    private String typeDisplay;
    private String comment;
    private LocalDateTime createdAt;
    private boolean resolved;
    private Boolean confirmed;
    private LocalDateTime resolvedAt;
    private String adminComment;
}
