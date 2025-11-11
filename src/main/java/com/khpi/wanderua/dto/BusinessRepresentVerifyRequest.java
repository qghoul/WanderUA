package com.khpi.wanderua.dto;

import com.khpi.wanderua.entity.Document;
import com.khpi.wanderua.enums.VerifyRequestType;
import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class BusinessRepresentVerifyRequest {
    String businessName;
    String businessDescription;
    String representFullName;
    //Set<Document> documentSet;
    Set<Long> documentIds;
    VerifyRequestType verifyRequestType;
    String comment;
}
