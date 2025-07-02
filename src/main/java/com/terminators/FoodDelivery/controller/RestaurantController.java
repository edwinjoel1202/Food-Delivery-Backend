package com.terminators.FoodDelivery.controller;

import com.terminators.FoodDelivery.model.Restaurant;
import com.terminators.FoodDelivery.service.ImageService;
import com.terminators.FoodDelivery.service.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private ImageService imageService;

    @PostMapping
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<Restaurant> createRestaurant(@RequestPart("restaurant") @Valid Restaurant restaurant,
                                                       @RequestPart(value = "image", required = false) MultipartFile image,
                                                       @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        if (image != null) {
            String imageUrl = imageService.uploadImage(image);
            restaurant.setImageUrl(imageUrl);
        }
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
                                                       @RequestBody @Valid Restaurant updatedRestaurant,
                                                       @AuthenticationPrincipal UserDetails userDetails) {
        Restaurant restaurant = restaurantService.updateRestaurant(restaurantId, updatedRestaurant, userDetails.getUsername());
        return ResponseEntity.ok(restaurant);
    }

    @PutMapping("/{id}/coordinates")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<Restaurant> updateRestaurantCoordinates(@PathVariable("id") Long restaurantId,
                                                                  @RequestBody CoordinatesRequest coordinates,
                                                                  @AuthenticationPrincipal UserDetails userDetails) {
        Restaurant restaurant = restaurantService.getRestaurantsByOwner(userDetails.getUsername()).stream()
                .filter(r -> r.getRestaurantId().equals(restaurantId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Restaurant not found or not owned by user"));
        restaurant.setLatitude(coordinates.getLatitude());
        restaurant.setLongitude(coordinates.getLongitude());
        Restaurant updatedRestaurant = restaurantService.updateRestaurant(restaurantId, restaurant, userDetails.getUsername());
        return ResponseEntity.ok(updatedRestaurant);
    }

    public static class CoordinatesRequest {
        private Double latitude;
        private Double longitude;

        public Double getLatitude() { return latitude; }
        public void setLatitude(Double latitude) { this.latitude = latitude; }
        public Double getLongitude() { return longitude; }
        public void setLongitude(Double longitude) { this.longitude = longitude; }
    }
}