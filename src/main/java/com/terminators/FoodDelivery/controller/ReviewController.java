package com.terminators.FoodDelivery.controller;

import com.terminators.FoodDelivery.model.Review;
import com.terminators.FoodDelivery.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/food-item/{foodItemId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ReviewResponseDTO> createReview(
            @PathVariable Long foodItemId,
            @RequestBody ReviewRequestDTO reviewRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        Review review = reviewService.createReview(
                foodItemId,
                reviewRequest.getRating(),
                reviewRequest.getComment(),
                userDetails.getUsername()
        );
        return ResponseEntity.ok(new ReviewResponseDTO(review));
    }

    @GetMapping("/food-item/{foodItemId}")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByFoodItem(@PathVariable Long foodItemId) {
        List<Review> reviews = reviewService.getReviewsByFoodItem(foodItemId);
        List<ReviewResponseDTO> reviewDTOs = reviews.stream()
                .map(ReviewResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(reviewDTOs);
    }

    // DTO for review request
    public static class ReviewRequestDTO {
        private Integer rating;
        private String comment;

        public Integer getRating() { return rating; }
        public void setRating(Integer rating) { this.rating = rating; }
        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }
    }

    // DTO for review response
    public static class ReviewResponseDTO {
        private Long reviewId;
        private Long foodItemId;
        private Long customerId;
        private String customerName;
        private Integer rating;
        private String comment;

        public ReviewResponseDTO(Review review) {
            this.reviewId = review.getReviewId();
            this.foodItemId = review.getFoodItem().getFoodItemId();
            this.customerId = review.getCustomer().getUserId();
            this.customerName = review.getCustomer().getName();
            this.rating = review.getRating();
            this.comment = review.getComment();
        }

        // Getters and Setters
        public Long getReviewId() { return reviewId; }
        public void setReviewId(Long reviewId) { this.reviewId = reviewId; }
        public Long getFoodItemId() { return foodItemId; }
        public void setFoodItemId(Long foodItemId) { this.foodItemId = foodItemId; }
        public Long getCustomerId() { return customerId; }
        public void setCustomerId(Long customerId) { this.customerId = customerId; }
        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }
        public Integer getRating() { return rating; }
        public void setRating(Integer rating) { this.rating = rating; }
        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }
    }
}