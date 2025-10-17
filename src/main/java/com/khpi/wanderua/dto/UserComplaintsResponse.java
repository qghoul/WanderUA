package com.khpi.wanderua.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserComplaintsResponse {
    private List<AdvertisementComplaintResponse> advertisementComplaints;
    private List<ReviewComplaintResponse> reviewComplaints;
    private int totalCount;

    public UserComplaintsResponse(
            List<AdvertisementComplaintResponse> advertisementComplaints,
            List<ReviewComplaintResponse> reviewComplaints) {
        this.advertisementComplaints = advertisementComplaints;
        this.reviewComplaints = reviewComplaints;
        this.totalCount = advertisementComplaints.size() + reviewComplaints.size();
    }
}
