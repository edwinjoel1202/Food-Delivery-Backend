package com.terminators.FoodDelivery.repository;

import com.terminators.FoodDelivery.model.DeliveryPartner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryPartnerRepository extends JpaRepository<DeliveryPartner, Long> {
    // Custom query method
    DeliveryPartner findByEmail(String email);
    List<DeliveryPartner> findByStatus(String status);
}