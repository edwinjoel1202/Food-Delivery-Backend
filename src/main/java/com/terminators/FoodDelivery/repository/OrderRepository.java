package com.terminators.FoodDelivery.repository;

import com.terminators.FoodDelivery.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // Find orders by customer ID
    List<Order> findByCustomerUserId(Long customerId);

    // Find orders by restaurant ID
    List<Order> findByRestaurantRestaurantId(Long restaurantId);
}