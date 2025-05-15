package com.terminators.FoodDelivery.repository;

import com.terminators.FoodDelivery.model.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {
    List<FoodItem> findByRestaurantRestaurantId(Long restaurantId);

    @Query("SELECT fi FROM FoodItem fi WHERE LOWER(fi.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<FoodItem> findByKeyword(@Param("keyword") String keyword);
}