package com.terminators.FoodDelivery.repository;

import com.terminators.FoodDelivery.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    // Custom query methods
    List<Feedback> findByUserUserId(Long userId);
    List<Feedback> findByRestaurantRestaurantId(Long restaurantId);
}