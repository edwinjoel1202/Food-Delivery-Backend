package com.terminators.FoodDelivery.repository;

import com.terminators.FoodDelivery.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    // Find all cart items for a user
    List<CartItem> findByUserUserId(Long userId);

    // Find a specific cart item by user and food item
    Optional<CartItem> findByUserUserIdAndFoodItemFoodItemId(Long userId, Long foodItemId);

    // Delete all cart items for a user
    void deleteByUserUserId(Long userId);
}