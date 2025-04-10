package com.terminators.FoodDelivery.controller;

import com.terminators.FoodDelivery.model.Restaurant;
import com.terminators.FoodDelivery.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @PostMapping
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<Restaurant> createRestaurant(@RequestBody Restaurant restaurant,
                                                       @AuthenticationPrincipal UserDetails userDetails) {
        Restaurant createdRestaurant = restaurantService.createRestaurant(restaurant, userDetails.getUsername());
        return ResponseEntity.ok(createdRestaurant);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<List<Restaurant>> getMyRestaurants(@AuthenticationPrincipal UserDetails userDetails) {
        List<Restaurant> restaurants = restaurantService.getRestaurantsByOwner(userDetails.getUsername());
        return ResponseEntity.ok(restaurants);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<Restaurant> updateRestaurant(@PathVariable("id") Long restaurantId,
                                                       @RequestBody Restaurant updatedRestaurant,
                                                       @AuthenticationPrincipal UserDetails userDetails) {
        Restaurant restaurant = restaurantService.updateRestaurant(restaurantId, updatedRestaurant, userDetails.getUsername());
        return ResponseEntity.ok(restaurant);
    }
}