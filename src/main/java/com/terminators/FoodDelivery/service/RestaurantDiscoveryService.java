package com.terminators.FoodDelivery.service;

import com.terminators.FoodDelivery.model.FoodItem;
import com.terminators.FoodDelivery.model.Restaurant;
import com.terminators.FoodDelivery.repository.FoodItemRepository;
import com.terminators.FoodDelivery.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RestaurantDiscoveryService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private FoodItemRepository foodItemRepository;

    public List<RestaurantDTO> searchRestaurants(String keyword, String cuisineType, String dietaryPreference, String sortBy) {
        List<Restaurant> restaurants;

        // Start with active restaurants
        if (keyword != null && !keyword.trim().isEmpty()) {
            // Search by keyword in restaurant name or cuisine
            restaurants = restaurantRepository.findByKeyword(keyword);

            // Also search food items and include their restaurants
            List<FoodItem> foodItems = foodItemRepository.findByKeyword(keyword);
            List<Restaurant> restaurantsFromFoodItems = foodItems.stream()
                    .map(FoodItem::getRestaurant)
                    .filter(r -> "ACTIVE".equals(r.getStatus()))
                    .distinct()
                    .collect(Collectors.toList());

            // Combine and deduplicate
            restaurants = new ArrayList<>(restaurants);
            restaurants.addAll(restaurantsFromFoodItems);
            restaurants = restaurants.stream().distinct().collect(Collectors.toList());
        } else {
            restaurants = restaurantRepository.findByActiveStatus();
        }

        // Apply filters
        if (cuisineType != null && !cuisineType.trim().isEmpty()) {
            restaurants = restaurants.stream()
                    .filter(r -> r.getCuisineType().equalsIgnoreCase(cuisineType))
                    .collect(Collectors.toList());
        }

        if (dietaryPreference != null && !dietaryPreference.trim().isEmpty()) {
            restaurants = restaurants.stream()
                    .filter(r -> r.getDietaryPreference().equalsIgnoreCase(dietaryPreference))
                    .collect(Collectors.toList());
        }

        // Apply sorting
        if ("name".equalsIgnoreCase(sortBy)) {
            restaurants.sort(Comparator.comparing(Restaurant::getName, String.CASE_INSENSITIVE_ORDER));
        }
        // Default sorting is by relevance (no change needed since keyword search already prioritizes)

        return restaurants.stream().map(RestaurantDTO::new).collect(Collectors.toList());
    }

    public List<RestaurantDTO> getPopularRestaurants() {
        List<Restaurant> popularRestaurants = restaurantRepository.findPopularRestaurants();
        return popularRestaurants.stream()
                .limit(10) // Limit to top 10 popular restaurants
                .map(RestaurantDTO::new)
                .collect(Collectors.toList());
    }

    public static class RestaurantDTO {
        private Long restaurantId;
        private String name;
        private String location;
        private String imageUrl;
        private String cuisineType;
        private String dietaryPreference;

        public RestaurantDTO(Restaurant restaurant) {
            this.restaurantId = restaurant.getRestaurantId();
            this.name = restaurant.getName();
            this.location = restaurant.getLocation();
            this.imageUrl = restaurant.getImageUrl();
            this.cuisineType = restaurant.getCuisineType();
            this.dietaryPreference = restaurant.getDietaryPreference();
        }

        // Getters and Setters
        public Long getRestaurantId() { return restaurantId; }
        public void setRestaurantId(Long restaurantId) { this.restaurantId = restaurantId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public String getCuisineType() { return cuisineType; }
        public void setCuisineType(String cuisineType) { this.cuisineType = cuisineType; }
        public String getDietaryPreference() { return dietaryPreference; }
        public void setDietaryPreference(String dietaryPreference) { this.dietaryPreference = dietaryPreference; }
    }
}