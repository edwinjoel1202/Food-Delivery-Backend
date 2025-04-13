package com.terminators.FoodDelivery.repository;

import com.terminators.FoodDelivery.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}