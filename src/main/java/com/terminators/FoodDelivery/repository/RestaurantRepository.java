package com.terminators.FoodDelivery.repository;

import com.terminators.FoodDelivery.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    List<Restaurant> findByOwnerUserId(Long ownerId);

    @Query("SELECT r FROM Restaurant r WHERE r.status = 'ACTIVE'")
    List<Restaurant> findByActiveStatus();

    @Query("SELECT r FROM Restaurant r WHERE r.status = 'ACTIVE' AND (LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(r.cuisineType) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Restaurant> findByKeyword(@Param("keyword") String keyword);

    @Query("SELECT r FROM Restaurant r WHERE r.status = 'ACTIVE' AND LOWER(r.cuisineType) = LOWER(:cuisineType)")
    List<Restaurant> findByCuisineType(@Param("cuisineType") String cuisineType);

    @Query("SELECT r FROM Restaurant r WHERE r.status = 'ACTIVE' AND LOWER(r.dietaryPreference) = LOWER(:dietaryPreference)")
    List<Restaurant> findByDietaryPreference(@Param("dietaryPreference") String dietaryPreference);

    @Query("SELECT r FROM Restaurant r JOIN Order o ON r.restaurantId = o.restaurant.restaurantId WHERE r.status = 'ACTIVE' GROUP BY r ORDER BY COUNT(o) DESC")
    List<Restaurant> findPopularRestaurants();
}