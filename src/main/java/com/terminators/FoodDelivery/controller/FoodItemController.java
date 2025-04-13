package com.terminators.FoodDelivery.controller;

import com.terminators.FoodDelivery.model.FoodItem;
import com.terminators.FoodDelivery.service.FoodItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/food-items")
public class FoodItemController {

    @Autowired
    private FoodItemService foodItemService;

    @PostMapping
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<FoodItem> createFoodItem(@RequestPart("foodItem") @Valid FoodItem foodItem,
                                                   @RequestPart("restaurantId") String restaurantId,
                                                   @RequestPart(value = "image", required = false) org.springframework.web.multipart.MultipartFile image,
                                                   @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        FoodItem createdFoodItem = foodItemService.createFoodItem(foodItem, Long.valueOf(restaurantId), userDetails.getUsername(), image);
        return ResponseEntity.ok(createdFoodItem);
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<FoodItem>> getFoodItemsByRestaurant(@PathVariable Long restaurantId) {
        List<FoodItem> foodItems = foodItemService.getFoodItemsByRestaurant(restaurantId);
        return ResponseEntity.ok(foodItems);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<FoodItem> updateFoodItem(@PathVariable("id") Long foodItemId,
                                                   @RequestPart("foodItem") @Valid FoodItem updatedFoodItem,
                                                   @RequestPart(value = "image", required = false) org.springframework.web.multipart.MultipartFile image,
                                                   @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        FoodItem foodItem = foodItemService.updateFoodItem(foodItemId, updatedFoodItem, userDetails.getUsername(), image);
        return ResponseEntity.ok(foodItem);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<Void> deleteFoodItem(@PathVariable("id") Long foodItemId,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        foodItemService.deleteFoodItem(foodItemId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}