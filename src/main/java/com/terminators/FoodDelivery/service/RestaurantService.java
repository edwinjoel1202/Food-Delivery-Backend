package com.terminators.FoodDelivery.service;

import com.terminators.FoodDelivery.model.Restaurant;
import com.terminators.FoodDelivery.model.User;
import com.terminators.FoodDelivery.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private UserService userService;

    public Restaurant createRestaurant(Restaurant restaurant, String ownerEmail) {
        User owner = userService.getUserByEmail(ownerEmail);
        if (!"RESTAURANT_OWNER".equals(owner.getRole())) {
            throw new RuntimeException("Only Restaurant Owners can create restaurants");
        }
        restaurant.setOwner(owner);
        return restaurantRepository.save(restaurant);
    }

    public List<Restaurant> getRestaurantsByOwner(String ownerEmail) {
        User owner = userService.getUserByEmail(ownerEmail);
        return restaurantRepository.findByOwnerUserId(owner.getUserId());
    }

    public Restaurant updateRestaurant(Long restaurantId, Restaurant updatedRestaurant, String ownerEmail) {
        Restaurant existingRestaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));
        User owner = userService.getUserByEmail(ownerEmail);
        if (!existingRestaurant.getOwner().getUserId().equals(owner.getUserId())) {
            throw new RuntimeException("You can only update your own restaurants");
        }
        if (updatedRestaurant.getName() != null) {
            existingRestaurant.setName(updatedRestaurant.getName());
        }
        if (updatedRestaurant.getLocation() != null) {
            existingRestaurant.setLocation(updatedRestaurant.getLocation());
        }
        return restaurantRepository.save(existingRestaurant);
    }
}