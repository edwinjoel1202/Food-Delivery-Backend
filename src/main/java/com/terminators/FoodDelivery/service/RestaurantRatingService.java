package com.terminators.FoodDelivery.service;

import com.terminators.FoodDelivery.model.FoodItem;
import com.terminators.FoodDelivery.model.Restaurant;
import com.terminators.FoodDelivery.model.Review;
import com.terminators.FoodDelivery.repository.RestaurantRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RestaurantRatingService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Transactional(readOnly = true)
    public RatingSummary calculateRatingSummary(Restaurant restaurant) {
        // Ensure foodItems is already fetched
        List<FoodItem> foodItems = restaurant.getFoodItems();

        // Initialize the reviews collection for each food item
        List<Review> allReviews = new ArrayList<>();
        for (FoodItem foodItem : foodItems) {
            Hibernate.initialize(foodItem.getReviews());
            allReviews.addAll(foodItem.getReviews());
        }

        double averageRating = 0.0;
        int totalReviews = allReviews.size();

        if (totalReviews > 0) {
            double totalRating = allReviews.stream()
                    .mapToDouble(Review::getRating)
                    .sum();
            averageRating = totalRating / totalReviews;
        }

        return new RatingSummary(averageRating, totalReviews);
    }

    @Transactional(readOnly = true)
    public List<ReviewDTO> getRecentReviews(Long restaurantId, int limit) {
        Restaurant restaurant = restaurantRepository.findByIdWithFoodItems(restaurantId);
        if (restaurant == null) {
            throw new RuntimeException("Restaurant not found with id: " + restaurantId);
        }

        // Initialize reviews for each food item
        List<Review> allReviews = new ArrayList<>();
        for (FoodItem foodItem : restaurant.getFoodItems()) {
            Hibernate.initialize(foodItem.getReviews());
            allReviews.addAll(foodItem.getReviews());
        }

        return allReviews.stream()
                .sorted(Comparator.comparing(Review::getCreatedAt).reversed())
                .limit(limit)
                .map(review -> new ReviewDTO(review))
                .collect(Collectors.toList());
    }

    public static class RatingSummary {
        private double averageRating;
        private int totalReviews;

        public RatingSummary(double averageRating, int totalReviews) {
            this.averageRating = averageRating;
            this.totalReviews = totalReviews;
        }

        public double getAverageRating() { return averageRating; }
        public void setAverageRating(double averageRating) { this.averageRating = averageRating; }
        public int getTotalReviews() { return totalReviews; }
        public void setTotalReviews(int totalReviews) { this.totalReviews = totalReviews; }
    }

    public static class ReviewDTO {
        private Long reviewId;
        private Long foodItemId;
        private String foodItemName;
        private Long customerId;
        private String customerName;
        private Integer rating;
        private String comment;

        public ReviewDTO(Review review) {
            this.reviewId = review.getReviewId();
            this.foodItemId = review.getFoodItem().getFoodItemId();
            this.foodItemName = review.getFoodItem().getName();
            this.customerId = review.getCustomer().getUserId();
            this.customerName = review.getCustomer().getName();
            this.rating = review.getRating();
            this.comment = review.getComment();
        }

        public Long getReviewId() { return reviewId; }
        public void setReviewId(Long reviewId) { this.reviewId = reviewId; }
        public Long getFoodItemId() { return foodItemId; }
        public void setFoodItemId(Long foodItemId) { this.foodItemId = foodItemId; }
        public String getFoodItemName() { return foodItemName; }
        public void setFoodItemName(String foodItemName) { this.foodItemName = foodItemName; }
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