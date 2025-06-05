package com.terminators.FoodDelivery.controller;

import com.terminators.FoodDelivery.model.Restaurant;
import com.terminators.FoodDelivery.service.FavoriteRestaurantService;
import com.terminators.FoodDelivery.service.RestaurantDiscoveryService;
import com.terminators.FoodDelivery.service.RestaurantRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteRestaurantController {

    @Autowired
    private FavoriteRestaurantService favoriteRestaurantService;

    @PostMapping("/{restaurantId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> addFavoriteRestaurant(
            @PathVariable Long restaurantId,
            @AuthenticationPrincipal UserDetails userDetails) {
        favoriteRestaurantService.addFavoriteRestaurant(restaurantId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{restaurantId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> removeFavoriteRestaurant(
            @PathVariable Long restaurantId,
            @AuthenticationPrincipal UserDetails userDetails) {
        favoriteRestaurantService.removeFavoriteRestaurant(restaurantId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<RestaurantDiscoveryService.RestaurantDTO>> getFavoriteRestaurants(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<Restaurant> restaurants = favoriteRestaurantService.getFavoriteRestaurants(userDetails.getUsername());
        List<RestaurantDiscoveryService.RestaurantDTO> restaurantDTOs = restaurants.stream()
                .map(restaurant -> {
                    RestaurantRatingService.RatingSummary ratingSummary = new RestaurantRatingService.RatingSummary(0.0, 0);
                    return new RestaurantDiscoveryService.RestaurantDTO(restaurant, ratingSummary.getAverageRating(), ratingSummary.getTotalReviews());
                })
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(restaurantDTOs);
    }
}