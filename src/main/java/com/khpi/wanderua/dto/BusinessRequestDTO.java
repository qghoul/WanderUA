package com.khpi.wanderua.dto;

import com.khpi.wanderua.entity.*;
import com.khpi.wanderua.enums.VerifyRequestType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class BusinessRequestDTO {
    Long id;
    Long userId;
    String username;
    Long businessId;
    String businessName;
    String businessDescription;
    String representFullName;
    VerifyRequestType verifyRequestType;
    String comment;
    Boolean resolved;
    Boolean confirmed;
    String adminComment;
    LocalDateTime createdAt;
    LocalDateTime resolvedAt;

    @Builder.Default
    private List<DocumentDTO> documentSet = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DocumentDTO {
        private Long id;
        private String fileName;
        private String fileType;
        private String filePath;
    }
}
