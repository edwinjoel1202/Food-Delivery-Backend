package com.terminators.FoodDelivery.repository;

import com.terminators.FoodDelivery.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    Delivery findByOrderOrderId(Long orderId);
    List<Delivery> findByDeliveryPersonUserId(Long userId);
}