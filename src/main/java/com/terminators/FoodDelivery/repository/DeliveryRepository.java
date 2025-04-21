package com.terminators.FoodDelivery.repository;

import com.terminators.FoodDelivery.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    Delivery findByOrderOrderId(Long orderId);
}