package com.terminators.FoodDelivery.repository;

import com.terminators.FoodDelivery.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // Custom query methods
    List<Order> findByUserUserId(Long userId);
    List<Order> findByRestaurantRestaurantId(Long restaurantId);
    List<Order> findByStatus(String status);
}