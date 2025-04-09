package com.terminators.FoodDelivery.repository;

import com.terminators.FoodDelivery.model.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {
    // Custom query method to find food items by restaurant
    List<FoodItem> findByRestaurantRestaurantId(Long restaurantId);
}