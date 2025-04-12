package com.terminators.FoodDelivery.service;

import com.terminators.FoodDelivery.model.Restaurant;
import com.terminators.FoodDelivery.model.User;
import com.terminators.FoodDelivery.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private UserService userService;

    public Restaurant createRestaurant(Restaurant restaurant, String ownerEmail) {
        User owner = userService.getUserByEmail(ownerEmail);
        if (owner == null) {
            throw new RuntimeException("User not found with email: " + ownerEmail);
        }
        if (!"RESTAURANT_OWNER".equals(owner.getRole())) {
            throw new RuntimeException("Only Restaurant Owners can create restaurants");
        }
        restaurant.setOwner(owner);
        restaurant.setCreatedAt(LocalDateTime.now());
        restaurant.setUpdatedAt(LocalDateTime.now());
        return restaurantRepository.save(restaurant);
    }

    public List<Restaurant> getRestaurantsByOwner(String ownerEmail) {
        User owner = userService.getUserByEmail(ownerEmail);
        if (owner == null) {
            throw new RuntimeException("User not found with email: " + ownerEmail);
        }
        return restaurantRepository.findByOwnerUserId(owner.getUserId());
    }

    public Restaurant updateRestaurant(Long restaurantId, Restaurant updatedRestaurant, String ownerEmail) {
        Restaurant existingRestaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + restaurantId));
        User owner = userService.getUserByEmail(ownerEmail);
        if (owner == null) {
            throw new RuntimeException("User not found with email: " + ownerEmail);
        }
        if (!existingRestaurant.getOwner().getUserId().equals(owner.getUserId())) {
            throw new RuntimeException("You can only update your own restaurants");
        }
        if (updatedRestaurant.getName() != null && !updatedRestaurant.getName().isBlank()) {
            existingRestaurant.setName(updatedRestaurant.getName());
        }
        if (updatedRestaurant.getLocation() != null && !updatedRestaurant.getLocation().isBlank()) {
            existingRestaurant.setLocation(updatedRestaurant.getLocation());
        }
        if (updatedRestaurant.getStatus() != null && !updatedRestaurant.getStatus().isBlank()) {
            existingRestaurant.setStatus(updatedRestaurant.getStatus());
        }
        existingRestaurant.setUpdatedAt(LocalDateTime.now());
        return restaurantRepository.save(existingRestaurant);
    }
}