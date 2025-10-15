package com.khpi.wanderua.service;

import com.khpi.wanderua.repository.*;
import com.khpi.wanderua.entity.*;
import com.khpi.wanderua.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComplaintService {
    private final AdvertisementComplaintRepository advertisementComplaintRepository;
    private final ReviewComplaintRepository reviewComplaintRepository;
    private final AdvertisementRepository advertisementRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    @Transactional
    public AdvertisementComplaintResponse createAdvertisementComplaint(
            Long advertisementId,
            Long userId,
            ComplaintRequest request){
        log.info("Creating complaint for advertisement {} by user {}", advertisementId, userId);

        if (advertisementComplaintRepository.existsByUserIdAndAdvertisementId(userId, advertisementId)) {
            throw new IllegalStateException("Ви вже подали скаргу на це оголошення");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Користувач не знайдений"));

        Advertisement advertisement = advertisementRepository.findById(advertisementId)
                .orElseThrow(() -> new IllegalArgumentException("Оголошення не знайдено"));

        AdvertisementComplaint complaint = new AdvertisementComplaint();
        complaint.setUser(user);
        complaint.setAdvertisement(advertisement);
        complaint.setType(request.getType());
        complaint.setComment(request.getComment());

        AdvertisementComplaint saved = advertisementComplaintRepository.save(complaint);
        log.info("Advertisement complaint created with id: {}", saved.getId());

        return mapToAdvertisementComplaintResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<AdvertisementComplaintResponse> getAllAdvertisementComplaints() {
        log.info("Fetching all advertisement complaints");
        return advertisementComplaintRepository.findAll().stream()
                .map(this::mapToAdvertisementComplaintResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AdvertisementComplaintResponse> getUnresolvedAdvertisementComplaints() {
        log.info("Fetching unresolved advertisement complaints");
        return advertisementComplaintRepository.findByResolved(false).stream()
                .map(this::mapToAdvertisementComplaintResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AdvertisementComplaintResponse> getResolvedAdvertisementComplaints() {
        log.info("Fetching resolved advertisement complaints");
        return advertisementComplaintRepository.findByResolved(true).stream()
                .map(this::mapToAdvertisementComplaintResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReviewComplaintResponse createReviewComplaint(
            Long reviewId,
            Long userId,
            ComplaintRequest request) {

        log.info("Creating complaint for review {} by user {}", reviewId, userId);

        if (reviewComplaintRepository.existsByUserIdAndReviewId(userId, reviewId)) {
            throw new IllegalStateException("Ви вже подали скаргу на цей відгук");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Користувач не знайдений"));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Відгук не знайдено"));

        ReviewComplaint complaint = new ReviewComplaint();
        complaint.setUser(user);
        complaint.setReview(review);
        complaint.setType(request.getType());
        complaint.setComment(request.getComment());

        ReviewComplaint saved = reviewComplaintRepository.save(complaint);
        log.info("Review complaint created with id: {}", saved.getId());

        return mapToReviewComplaintResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ReviewComplaintResponse> getAllReviewComplaints() {
        log.info("Fetching all review complaints");
        return reviewComplaintRepository.findAll().stream()
                .map(this::mapToReviewComplaintResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReviewComplaintResponse> getUnresolvedReviewComplaints() {
        log.info("Fetching unresolved review complaints");
        return reviewComplaintRepository.findByResolved(false).stream()
                .map(this::mapToReviewComplaintResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReviewComplaintResponse> getResolvedReviewComplaints() {
        log.info("Fetching resolved review complaints");
        return reviewComplaintRepository.findByResolved(true).stream()
                .map(this::mapToReviewComplaintResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AdvertisementComplaintResponse resolveAdvertisementComplaint(
            Long complaintId,
            boolean confirmed,
            String adminComment) {

        log.info("Resolving advertisement complaint {} with confirmation: {}", complaintId, confirmed);

        AdvertisementComplaint complaint = advertisementComplaintRepository.findById(complaintId)
                .orElseThrow(() -> new IllegalArgumentException("Скаргу не знайдено"));

        complaint.setResolved(true);
        complaint.setResolvedAt(LocalDateTime.now());
        complaint.setConfirmed(confirmed);
        complaint.setAdminComment(adminComment);

        if (confirmed) {
            // Soft delete (isActive = false)
            Advertisement advertisement = complaint.getAdvertisement();
            advertisement.setActive(false);
            advertisementRepository.save(advertisement);
            log.info("Advertisement {} marked as inactive due to confirmed complaint", advertisement.getId());
        }

        AdvertisementComplaint saved = advertisementComplaintRepository.save(complaint);
        return mapToAdvertisementComplaintResponse(saved);
    }

    @Transactional
    public ReviewComplaintResponse resolveReviewComplaint(
            Long complaintId,
            boolean confirmed,
            String adminComment) {

        log.info("Resolving review complaint {} with confirmation: {}", complaintId, confirmed);

        ReviewComplaint complaint = reviewComplaintRepository.findById(complaintId)
                .orElseThrow(() -> new IllegalArgumentException("Скаргу не знайдено"));

        complaint.setResolved(true);
        complaint.setResolvedAt(LocalDateTime.now());
        complaint.setConfirmed(confirmed);
        complaint.setAdminComment(adminComment);

        if (confirmed) {
            // Soft delete
            Review review = complaint.getReview();
            review.setActive(false);
            reviewRepository.save(review);
            log.info("Review {} marked as inactive due to confirmed complaint", review.getId());
        }

        ReviewComplaint saved = reviewComplaintRepository.save(complaint);
        return mapToReviewComplaintResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<AdvertisementComplaintResponse> getAdvertisementComplaintsByStatus(String status) {
        List<AdvertisementComplaint> complaints;

        switch (status.toLowerCase()) {
            case "unresolved":
                complaints = advertisementComplaintRepository.findByResolved(false);
                break;
            case "resolved":
                complaints = advertisementComplaintRepository.findByResolved(true);
                break;
            case "confirmed":
                complaints = advertisementComplaintRepository.findByConfirmed(true);
                break;
            case "rejected":
                complaints = advertisementComplaintRepository.findByConfirmed(false);
                break;
            default:
                complaints = advertisementComplaintRepository.findAll();
        }

        return complaints.stream()
                .map(this::mapToAdvertisementComplaintResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReviewComplaintResponse> getReviewComplaintsByStatus(String status) {
        List<ReviewComplaint> complaints;

        switch (status.toLowerCase()) {
            case "unresolved":
                complaints = reviewComplaintRepository.findByResolved(false);
                break;
            case "resolved":
                complaints = reviewComplaintRepository.findByResolved(true);
                break;
            case "confirmed":
                complaints = reviewComplaintRepository.findByConfirmed(true);
                break;
            case "rejected":
                complaints = reviewComplaintRepository.findByConfirmed(false);
                break;
            default:
                complaints = reviewComplaintRepository.findAll();
        }

        return complaints.stream()
                .map(this::mapToReviewComplaintResponse)
                .collect(Collectors.toList());
    }

    private AdvertisementComplaintResponse mapToAdvertisementComplaintResponse(AdvertisementComplaint complaint) {
        AdvertisementComplaintResponse response = new AdvertisementComplaintResponse();
        response.setId(complaint.getId());
        response.setUserId(complaint.getUser().getId());
        response.setUsername(complaint.getUser().getUsername());
        response.setAdvertisementId(complaint.getAdvertisement().getId());
        response.setAdvertisementTitle(complaint.getAdvertisement().getName());
        response.setType(complaint.getType());
        response.setTypeDisplay(complaint.getType().getDisplayName());
        response.setComment(complaint.getComment());
        response.setCreatedAt(complaint.getCreatedAt());
        response.setResolved(complaint.isResolved());
        response.setConfirmed(complaint.getConfirmed());
        response.setResolvedAt(complaint.getResolvedAt());
        response.setAdminComment(complaint.getAdminComment());
        return response;
    }

    private ReviewComplaintResponse mapToReviewComplaintResponse(ReviewComplaint complaint) {
        ReviewComplaintResponse response = new ReviewComplaintResponse();
        response.setId(complaint.getId());
        response.setUserId(complaint.getUser().getId());
        response.setUsername(complaint.getUser().getUsername());
        response.setReviewId(complaint.getReview().getId());
        response.setReviewTitle(complaint.getReview().getTitle());
        response.setReviewText(complaint.getReview().getComment());
        List<String> imageUrls = complaint.getReview().getImages().stream()
                .map(image -> "/images/reviews/" + image.getName())
                .collect(Collectors.toList());
        response.setReviewImageUrls(imageUrls);
        response.setAdvertisementId(complaint.getReview().getAdvertisement().getId());
        response.setAdvertisementTitle(complaint.getReview().getAdvertisement().getName());
        response.setType(complaint.getType());
        response.setTypeDisplay(complaint.getType().getDisplayName());
        response.setComment(complaint.getComment());
        response.setCreatedAt(complaint.getCreatedAt());
        response.setResolved(complaint.isResolved());
        response.setConfirmed(complaint.getConfirmed());
        response.setResolvedAt(complaint.getResolvedAt());
        response.setAdminComment(complaint.getAdminComment());
        return response;
    }
}
