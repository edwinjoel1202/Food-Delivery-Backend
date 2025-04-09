package com.terminators.FoodDelivery.repository;

import com.terminators.FoodDelivery.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    // Custom query method to find restaurants by owner
    List<Restaurant> findByOwnerUserId(Long ownerId);
}