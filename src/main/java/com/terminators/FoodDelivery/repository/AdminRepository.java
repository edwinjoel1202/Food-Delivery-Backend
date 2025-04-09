package com.terminators.FoodDelivery.repository;

import com.terminators.FoodDelivery.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    // Custom query method
    Admin findByEmail(String email);
}