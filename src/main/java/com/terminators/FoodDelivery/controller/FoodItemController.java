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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/food-items")
public class FoodItemController {

    @Autowired
    private FoodItemService foodItemService;

    @PostMapping
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<FoodItemDTO> createFoodItem(@RequestPart("foodItem") @Valid FoodItem foodItem,
                                                      @RequestPart("restaurantId") String restaurantId,
                                                      @RequestPart(value = "image", required = false) MultipartFile image,
                                                      @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        FoodItem createdFoodItem = foodItemService.createFoodItem(foodItem, Long.valueOf(restaurantId), userDetails.getUsername(), image);
        return ResponseEntity.ok(new FoodItemDTO(createdFoodItem));
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<FoodItemDTO>> getFoodItemsByRestaurant(@PathVariable Long restaurantId) {
        List<FoodItem> foodItems = foodItemService.getFoodItemsByRestaurant(restaurantId);
        List<FoodItemDTO> foodItemDTOs = foodItems.stream()
                .map(FoodItemDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(foodItemDTOs);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<FoodItemDTO> updateFoodItem(@PathVariable("id") Long foodItemId,
                                                      @RequestPart("foodItem") @Valid FoodItem updatedFoodItem,
                                                      @RequestPart(value = "image", required = false) MultipartFile image,
                                                      @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        FoodItem foodItem = foodItemService.updateFoodItem(foodItemId, updatedFoodItem, userDetails.getUsername(), image);
        return ResponseEntity.ok(new FoodItemDTO(foodItem));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<Void> deleteFoodItem(@PathVariable("id") Long foodItemId,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        foodItemService.deleteFoodItem(foodItemId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}