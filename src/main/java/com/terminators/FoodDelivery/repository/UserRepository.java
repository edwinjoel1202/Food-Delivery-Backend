package com.terminators.FoodDelivery.repository;

import com.terminators.FoodDelivery.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}