package com.terminators.FoodDelivery.repository;

import com.terminators.FoodDelivery.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByFoodItemFoodItemId(Long foodItemId);
    Optional<Review> findByFoodItemFoodItemIdAndCustomerUserId(Long foodItemId, Long customerId);
}