package com.terminators.FoodDelivery.repository;

import com.terminators.FoodDelivery.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // Custom query method
    Payment findByOrderOrderId(Long orderId);
}