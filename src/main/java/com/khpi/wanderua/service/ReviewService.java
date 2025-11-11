package com.khpi.wanderua.service;


import com.khpi.wanderua.dto.ReviewDTO;
import com.khpi.wanderua.entity.*;
import com.khpi.wanderua.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final AdvertisementRepository advertisementRepository;
    private final UserRepository userRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final ImageService imageService;
    private final ReviewUsefulRepository reviewUsefulRepository;

    public List<ReviewDTO> getReviewsDTOByAdvertisementId(Long advertisementId, Long currentUserId) {
        List<Review> reviews = reviewRepository.findByAdvertisementIdAndIsActiveTrueOrderByUsefulScoreDesc(advertisementId);
        return reviews.stream()
                .map(review -> convertToDTO(review, currentUserId))
                .collect(Collectors.toList());
    }
    public List<ReviewDTO> getReviewsDTOByAdvertisementIdSortedByDate(Long advertisementId, Long currentUserId) {
        List<Review> reviews = reviewRepository.findByAdvertisementIdAndIsActiveTrueOrderByDateDesc(advertisementId);
        return reviews.stream()
                .map(review -> convertToDTO(review, currentUserId))
                .collect(Collectors.toList());
    }
    public List<Review> getReviewsByAdvertisementId(Long advertisementId) {
        return reviewRepository.findByAdvertisementIdAndIsActiveTrueOrderByUsefulScoreDesc(advertisementId);
    }
    public Page<ReviewDTO> getReviewsDTOByAdvertisementId(Long advertisementId,  Long currentUserId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findByAdvertisementIdAndIsActiveTrueOrderByDateDesc(advertisementId, pageable);
        return reviews.map(review -> convertToDTO(review, currentUserId));
    }

    @Transactional
    public ReviewDTO createReview(ReviewDTO reviewDTO, Long userId, List<MultipartFile> images) {
        // Check if user already reviewed this advertisement
        if (reviewRepository.existsByUserIdAndAdvertisementId(userId, reviewDTO.getAdvertisementId())) {
            throw new IllegalStateException("Ви вже залишили відгук для цього оголошення");
        }

        // Get user and advertisement
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Користувача не знайдено"));

        Advertisement advertisement = advertisementRepository.findById(reviewDTO.getAdvertisementId())
                .orElseThrow(() -> new IllegalArgumentException("Оголошення не знайдено"));

        Review review = Review.builder()
                .user(user)
                .advertisement(advertisement)
                .rating(reviewDTO.getRating())
                .comment(reviewDTO.getComment())
                .title(reviewDTO.getTitle())
                .goWith(reviewDTO.getGoWith())
                .usefulScore(0)
                .build();

        review = reviewRepository.save(review);

        if (images != null && !images.isEmpty()) {
            List<String> savedImageNames = imageService.saveReviewImages(images, review.getId());
            for (String imageName : savedImageNames) {
                ReviewImage reviewImage = ReviewImage.builder()
                        .name(imageName)
                        .review(review)
                        .build();
                review.getImages().add(reviewImage);
                reviewImageRepository.save(reviewImage);
            }
        }

        updateAdvertisementRating(reviewDTO.getAdvertisementId());

        return convertToDTO(review, userId);
    }

    public ReviewDTO getReviewById(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Відгук не знайдено"));
        return convertToDTO(review, userId);
    }

    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Відгук не знайдено"));

        // Check if user owns this review
        if (!review.getUser().getId().equals(userId)) {
            throw new IllegalStateException("Ви не можете видалити цей відгук");
        }

        Long advertisementId = review.getAdvertisement().getId();

        List<String> imageNames = review.getImages().stream()
                .map(ReviewImage::getName)
                .toList();
        imageService.deleteReviewImages(imageNames);

        reviewRepository.delete(review);

        updateAdvertisementRating(advertisementId);

        Advertisement advert = advertisementRepository.findById(advertisementId).get();
        advert.setRatingsCount(advert.getRatingsCount() - 1);
        advertisementRepository.save(advert);
    }

    @Transactional
    public void markReviewAsUseful(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Відгук не знайдено"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Користувача не знайдено"));

        if (reviewUsefulRepository.existsByReviewIdAndUserId(reviewId, userId)) {
            throw new IllegalStateException("Ви вже відмітили цей відгук як корисний");
        }

        ReviewUseful reviewUseful = ReviewUseful.builder()
                .review(review)
                .user(user)
                .build();

        reviewUsefulRepository.save(reviewUseful);
        reviewRepository.incrementUsefulScore(reviewId);
        log.info("Review {} marked as useful by user {}", reviewId, userId);
    }
    @Transactional
    public void unmarkReviewAsUseful(Long reviewId, Long userId) {
        if (!reviewUsefulRepository.existsByReviewIdAndUserId(reviewId, userId)) {
            throw new IllegalStateException("Ви не відмічали цей відгук як корисний");
        }

        reviewUsefulRepository.deleteByReviewIdAndUserId(reviewId, userId);
        reviewRepository.decrementUsefulScore(reviewId);
        log.info("Review {} unmarked as useful by user {}", reviewId, userId);
    }

    public boolean hasUserMarkedAsUseful(Long reviewId, Long userId) {
        if (userId == null) {
            return false;
        }
        return reviewUsefulRepository.existsByReviewIdAndUserId(reviewId, userId);
    }

    public Map<Long, Boolean> getUserUsefulStatusForReviews(List<Long> reviewIds, Long userId) {
        if (userId == null || reviewIds == null || reviewIds.isEmpty()) {
            return Map.of();
        }

        List<ReviewUseful> usefulMarks = reviewUsefulRepository.findByReviewIdInAndUserId(reviewIds, userId);

        Set<Long> markedReviewIds = usefulMarks.stream()
                .map(ru -> ru.getReview().getId())
                .collect(Collectors.toSet());

        return reviewIds.stream()
                .collect(Collectors.toMap(
                        id -> id,
                        markedReviewIds::contains
                ));
    }

    public boolean canUserReview(Long userId, Long advertisementId) {
        return !reviewRepository.existsByUserIdAndAdvertisementId(userId, advertisementId);
    }

    private void updateAdvertisementRating(Long advertisementId) {
        Double averageRating = reviewRepository.findAverageRatingByAdvertisementId(advertisementId);
        Long ratingsCount = reviewRepository.countByAdvertisementIdAndIsActiveTrue(advertisementId);

        Advertisement advertisement = advertisementRepository.findById(advertisementId)
                .orElseThrow(() -> new IllegalArgumentException("Оголошення не знайдено"));

        advertisement.setReviewAvgRating(averageRating != null ? averageRating : 0.0);
        advertisement.setRatingsCount(ratingsCount.intValue());

        advertisementRepository.save(advertisement);
    }

    public List<Review> findByAdvertisementId(Long advertisementId) {
        return reviewRepository.findByAdvertisementIdAndIsActiveTrueOrderByUsefulScoreDesc(advertisementId);
    }


    private ReviewDTO convertToDTO(Review review, Long currentUserId) {
        List<String> imageUrls = review.getImages().stream()
                .map(image -> "/images/reviews/" + image.getName())
                .collect(Collectors.toList());

        boolean isAuthor = currentUserId != null && currentUserId.equals(review.getUser().getId());

        return ReviewDTO.builder()
                .id(review.getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .title(review.getTitle())
                .goWith(review.getGoWith())
                .date(review.getDate())
                .username(review.getUser().getUsername())
                .usefulScore(review.getUsefulScore())
                .imageUrls(imageUrls)
                .advertisementId(review.getAdvertisement().getId())
                .isAuthor(isAuthor)
                .build();
    }
}
