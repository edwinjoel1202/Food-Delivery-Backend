package com.terminators.FoodDelivery.repository;

import com.terminators.FoodDelivery.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // Custom query method to find order items by order
    List<OrderItem> findByOrderOrderId(Long orderId);
}