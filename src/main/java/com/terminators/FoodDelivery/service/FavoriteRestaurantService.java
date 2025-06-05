package com.terminators.FoodDelivery.service;

import com.terminators.FoodDelivery.model.FavoriteRestaurant;
import com.terminators.FoodDelivery.model.Restaurant;
import com.terminators.FoodDelivery.model.User;
import com.terminators.FoodDelivery.repository.FavoriteRestaurantRepository;
import com.terminators.FoodDelivery.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteRestaurantService {

    @Autowired
    private FavoriteRestaurantRepository favoriteRestaurantRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Transactional
    public FavoriteRestaurant addFavoriteRestaurant(Long restaurantId, String customerEmail) {
        User customer = userService.getUserByEmail(customerEmail);
        if (customer == null || !"CUSTOMER".equals(customer.getRole())) {
            throw new RuntimeException("Only customers can add favorite restaurants");
        }

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + restaurantId));

        // Check if already a favorite
        if (favoriteRestaurantRepository.findByUserUserIdAndRestaurantRestaurantId(customer.getUserId(), restaurantId).isPresent()) {
            throw new RuntimeException("Restaurant is already in your favorites");
        }

        FavoriteRestaurant favorite = new FavoriteRestaurant();
        favorite.setUser(customer);
        favorite.setRestaurant(restaurant);
        return favoriteRestaurantRepository.save(favorite);
    }

    @Transactional
    public void removeFavoriteRestaurant(Long restaurantId, String customerEmail) {
        User customer = userService.getUserByEmail(customerEmail);
        if (customer == null || !"CUSTOMER".equals(customer.getRole())) {
            throw new RuntimeException("Only customers can remove favorite restaurants");
        }

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + restaurantId));

        // Check if the restaurant is a favorite
        if (!favoriteRestaurantRepository.findByUserUserIdAndRestaurantRestaurantId(customer.getUserId(), restaurantId).isPresent()) {
            throw new RuntimeException("Restaurant is not in your favorites");
        }

        favoriteRestaurantRepository.deleteByUserUserIdAndRestaurantRestaurantId(customer.getUserId(), restaurantId);
    }

    public List<Restaurant> getFavoriteRestaurants(String customerEmail) {
        User customer = userService.getUserByEmail(customerEmail);
        if (customer == null || !"CUSTOMER".equals(customer.getRole())) {
            throw new RuntimeException("Only customers can view favorite restaurants");
        }

        return favoriteRestaurantRepository.findByUserUserId(customer.getUserId()).stream()
                .map(FavoriteRestaurant::getRestaurant)
                .collect(Collectors.toList());
    }
}