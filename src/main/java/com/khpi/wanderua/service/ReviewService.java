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
import java.util.stream.Collectors;


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

    public List<ReviewDTO> getReviewsByAdvertisementId(Long advertisementId) {
        List<Review> reviews = reviewRepository.findByAdvertisementIdOrderByUsefulScoreDesc(advertisementId);
        return reviews.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    public Page<ReviewDTO> getReviewsByAdvertisementId(Long advertisementId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findByAdvertisementIdOrderByDateDesc(advertisementId, pageable);
        return reviews.map(this::convertToDTO);
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

        return convertToDTO(review);
    }

    public ReviewDTO getReviewById(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Відгук не знайдено"));
        return convertToDTO(review);
    }

    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Відгук не знайдено"));

        // Check if user owns this review or is admin
        if (!review.getUser().getId().equals(userId)) {
            throw new IllegalStateException("Ви не можете видалити цей відгук");
        }

        Long advertisementId = review.getAdvertisement().getId();

        // Delete review images
        List<String> imageNames = review.getImages().stream()
                .map(ReviewImage::getName)
                .toList();
        imageService.deleteReviewImages(imageNames);

        reviewRepository.delete(review);

        // Update advertisement rating
        updateAdvertisementRating(advertisementId);
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
    }
    @Transactional
    public void unmarkReviewAsUseful(Long reviewId, Long userId) {
        if (!reviewUsefulRepository.existsByReviewIdAndUserId(reviewId, userId)) {
            throw new IllegalStateException("Ви не відмічали цей відгук як корисний");
        }
        reviewUsefulRepository.deleteByReviewIdAndUserId(reviewId, userId);
        reviewRepository.decrementUsefulScore(reviewId);
    }

    public boolean hasUserMarkedAsUseful(Long reviewId, Long userId) {
        return reviewUsefulRepository.existsByReviewIdAndUserId(reviewId, userId);
    }

    public boolean canUserReview(Long userId, Long advertisementId) {
        return !reviewRepository.existsByUserIdAndAdvertisementId(userId, advertisementId);
    }

    private void updateAdvertisementRating(Long advertisementId) {
        Double averageRating = reviewRepository.findAverageRatingByAdvertisementId(advertisementId);
        Long ratingsCount = reviewRepository.countByAdvertisementId(advertisementId);

        Advertisement advertisement = advertisementRepository.findById(advertisementId)
                .orElseThrow(() -> new IllegalArgumentException("Оголошення не знайдено"));

        advertisement.setReviewAvgRating(averageRating != null ? averageRating : 0.0);
        advertisement.setRatingsCount(ratingsCount.intValue());

        advertisementRepository.save(advertisement);
    }
    private ReviewDTO convertToDTO(Review review) {
        List<String> imageUrls = review.getImages().stream()
                .map(image -> "/images/reviews/" + image.getName())
                .collect(Collectors.toList());

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
                .build();
    }
}
