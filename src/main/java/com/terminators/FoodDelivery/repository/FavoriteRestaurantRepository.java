package com.terminators.FoodDelivery.repository;

import com.terminators.FoodDelivery.model.FavoriteRestaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRestaurantRepository extends JpaRepository<FavoriteRestaurant, Long> {
    Optional<FavoriteRestaurant> findByUserUserIdAndRestaurantRestaurantId(Long userId, Long restaurantId);
    List<FavoriteRestaurant> findByUserUserId(Long userId);
    void deleteByUserUserIdAndRestaurantRestaurantId(Long userId, Long restaurantId);
}