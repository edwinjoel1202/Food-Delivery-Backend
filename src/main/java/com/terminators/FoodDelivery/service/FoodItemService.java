package com.terminators.FoodDelivery.service;

import com.terminators.FoodDelivery.model.FoodItem;
import com.terminators.FoodDelivery.model.Restaurant;
import com.terminators.FoodDelivery.model.User;
import com.terminators.FoodDelivery.repository.FoodItemRepository;
import com.terminators.FoodDelivery.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FoodItemService {

    @Autowired
    private FoodItemRepository foodItemRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    public FoodItem createFoodItem(FoodItem foodItem, Long restaurantId, String ownerEmail, MultipartFile image) throws IOException {
        User owner = userService.getUserByEmail(ownerEmail);
        if (owner == null) {
            throw new RuntimeException("User not found with email: " + ownerEmail);
        }
        if (!"RESTAURANT_OWNER".equals(owner.getRole())) {
            throw new RuntimeException("Only Restaurant Owners can create food items");
        }

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + restaurantId));
        if (!restaurant.getOwner().getUserId().equals(owner.getUserId())) {
            throw new RuntimeException("You can only add food items to your own restaurants");
        }

        foodItem.setRestaurant(restaurant);
        if (image != null) {
            String imageUrl = imageService.uploadImage(image);
            foodItem.setImageUrl(imageUrl);
        }
        foodItem.setCreatedAt(LocalDateTime.now());
        foodItem.setUpdatedAt(LocalDateTime.now());
        return foodItemRepository.save(foodItem);
    }

    public List<FoodItem> getFoodItemsByRestaurant(Long restaurantId) {
        return foodItemRepository.findByRestaurantRestaurantId(restaurantId);
    }

    public FoodItem updateFoodItem(Long foodItemId, FoodItem updatedFoodItem, String ownerEmail, MultipartFile image) throws IOException {
        FoodItem existingFoodItem = foodItemRepository.findById(foodItemId)
                .orElseThrow(() -> new RuntimeException("Food item not found with id: " + foodItemId));

        User owner = userService.getUserByEmail(ownerEmail);
        if (owner == null) {
            throw new RuntimeException("User not found with email: " + ownerEmail);
        }
        if (!existingFoodItem.getRestaurant().getOwner().getUserId().equals(owner.getUserId())) {
            throw new RuntimeException("You can only update food items in your own restaurants");
        }

        if (updatedFoodItem.getName() != null && !updatedFoodItem.getName().isBlank()) {
            existingFoodItem.setName(updatedFoodItem.getName());
        }
        if (updatedFoodItem.getDescription() != null) {
            existingFoodItem.setDescription(updatedFoodItem.getDescription());
        }
        if (updatedFoodItem.getPrice() != null && updatedFoodItem.getPrice() > 0) {
            existingFoodItem.setPrice(updatedFoodItem.getPrice());
        }
        if (image != null) {
            String imageUrl = imageService.uploadImage(image);
            existingFoodItem.setImageUrl(imageUrl);
        }
        existingFoodItem.setUpdatedAt(LocalDateTime.now());
        return foodItemRepository.save(existingFoodItem);
    }

    public void deleteFoodItem(Long foodItemId, String ownerEmail) {
        FoodItem foodItem = foodItemRepository.findById(foodItemId)
                .orElseThrow(() -> new RuntimeException("Food item not found with id: " + foodItemId));

        User owner = userService.getUserByEmail(ownerEmail);
        if (owner == null) {
            throw new RuntimeException("User not found with email: " + ownerEmail);
        }
        if (!foodItem.getRestaurant().getOwner().getUserId().equals(owner.getUserId())) {
            throw new RuntimeException("You can only delete food items in your own restaurants");
        }

        foodItemRepository.delete(foodItem);
    }
}