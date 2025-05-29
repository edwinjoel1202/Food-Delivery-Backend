package com.terminators.FoodDelivery.controller;

import com.terminators.FoodDelivery.model.FoodItem;

import java.time.LocalDateTime;

public class FoodItemDTO {
    private Long foodItemId;
    private String name;
    private String description;
    private Double price;
    private Long restaurantId;
    private String restaurantName;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public FoodItemDTO(FoodItem foodItem) {
        this.foodItemId = foodItem.getFoodItemId();
        this.name = foodItem.getName();
        this.description = foodItem.getDescription();
        this.price = foodItem.getPrice();
        this.restaurantId = foodItem.getRestaurant().getRestaurantId();
        this.restaurantName = foodItem.getRestaurant().getName();
        this.imageUrl = foodItem.getImageUrl();
        this.createdAt = foodItem.getCreatedAt();
        this.updatedAt = foodItem.getUpdatedAt();
    }

    // Getters and Setters
    public Long getFoodItemId() { return foodItemId; }
    public void setFoodItemId(Long foodItemId) { this.foodItemId = foodItemId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Long getRestaurantId() { return restaurantId; }
    public void setRestaurantId(Long restaurantId) { this.restaurantId = restaurantId; }
    public String getRestaurantName() { return restaurantName; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}