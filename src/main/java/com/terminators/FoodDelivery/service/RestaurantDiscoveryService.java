package com.terminators.FoodDelivery.service;

import com.terminators.FoodDelivery.model.FoodItem;
import com.terminators.FoodDelivery.model.Restaurant;
import com.terminators.FoodDelivery.repository.FoodItemRepository;
import com.terminators.FoodDelivery.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private RestaurantRatingService ratingService;

    @Transactional(readOnly = true)
    public List<RestaurantDTO> searchRestaurants(String keyword, String cuisineType, String dietaryPreference, String sortBy) {
        List<Restaurant> restaurants;

        // Start with active restaurants, fetch with food items
        if (keyword != null && !keyword.trim().isEmpty()) {
            // Search by keyword in restaurant name or cuisine
            restaurants = restaurantRepository.findByKeywordWithFoodItems(keyword);

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
            restaurants = restaurantRepository.findByActiveStatusWithFoodItems();
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
        } else if ("rating".equalsIgnoreCase(sortBy)) {
            restaurants.sort((r1, r2) -> {
                RestaurantRatingService.RatingSummary summary1 = ratingService.calculateRatingSummary(r1);
                RestaurantRatingService.RatingSummary summary2 = ratingService.calculateRatingSummary(r2);
                return Double.compare(summary2.getAverageRating(), summary1.getAverageRating());
            });
        }
        // Default sorting is by relevance (no change needed since keyword search already prioritizes)

        return restaurants.stream().map(this::toRestaurantDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RestaurantDTO> getPopularRestaurants() {
        List<Restaurant> popularRestaurants = restaurantRepository.findPopularRestaurants();
        // Fetch food items for rating calculation
        popularRestaurants = popularRestaurants.stream()
                .map(r -> restaurantRepository.findByIdWithFoodItems(r.getRestaurantId()))
                .collect(Collectors.toList());
        return popularRestaurants.stream()
                .limit(10) // Limit to top 10 popular restaurants
                .map(this::toRestaurantDTO)
                .collect(Collectors.toList());
    }

    private RestaurantDTO toRestaurantDTO(Restaurant restaurant) {
        RestaurantRatingService.RatingSummary ratingSummary = ratingService.calculateRatingSummary(restaurant);
        return new RestaurantDTO(restaurant, ratingSummary.getAverageRating(), ratingSummary.getTotalReviews());
    }

    public static class RestaurantDTO {
        private Long restaurantId;
        private String name;
        private String location;
        private String imageUrl;
        private String cuisineType;
        private String dietaryPreference;
        private double averageRating;
        private int totalReviews;

        public RestaurantDTO(Restaurant restaurant, double averageRating, int totalReviews) {
            this.restaurantId = restaurant.getRestaurantId();
            this.name = restaurant.getName();
            this.location = restaurant.getLocation();
            this.imageUrl = restaurant.getImageUrl();
            this.cuisineType = restaurant.getCuisineType();
            this.dietaryPreference = restaurant.getDietaryPreference();
            this.averageRating = averageRating;
            this.totalReviews = totalReviews;
        }

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
        public double getAverageRating() { return averageRating; }
        public void setAverageRating(double averageRating) { this.averageRating = averageRating; }
        public int getTotalReviews() { return totalReviews; }
        public void setTotalReviews(int totalReviews) { this.totalReviews = totalReviews; }
    }
}