package com.terminators.FoodDelivery.controller;

import com.terminators.FoodDelivery.service.RestaurantDiscoveryService;
import com.terminators.FoodDelivery.service.RestaurantRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discovery")
public class RestaurantDiscoveryController {

    @Autowired
    private RestaurantDiscoveryService discoveryService;

    @Autowired
    private RestaurantRatingService ratingService;

    @GetMapping("/search")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<RestaurantDiscoveryService.RestaurantDTO>> searchRestaurants(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String cuisineType,
            @RequestParam(required = false) String dietaryPreference,
            @RequestParam(required = false, defaultValue = "relevance") String sortBy) {
        List<RestaurantDiscoveryService.RestaurantDTO> restaurants = discoveryService.searchRestaurants(keyword, cuisineType, dietaryPreference, sortBy);
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping("/popular")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<RestaurantDiscoveryService.RestaurantDTO>> getPopularRestaurants() {
        List<RestaurantDiscoveryService.RestaurantDTO> popularRestaurants = discoveryService.getPopularRestaurants();
        return ResponseEntity.ok(popularRestaurants);
    }

    @GetMapping("/{restaurantId}/reviews")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<RestaurantRatingService.ReviewDTO>> getRestaurantReviews(
            @PathVariable Long restaurantId,
            @RequestParam(defaultValue = "5") int limit) {
        List<RestaurantRatingService.ReviewDTO> reviews = ratingService.getRecentReviews(restaurantId, limit);
        return ResponseEntity.ok(reviews);
    }
}