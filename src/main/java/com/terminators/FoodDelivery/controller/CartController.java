package com.terminators.FoodDelivery.controller;

import com.terminators.FoodDelivery.model.CartItem;
import com.terminators.FoodDelivery.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/items")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CartItemDTO> addToCart(@RequestBody CartItemRequest request,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        CartItem cartItem = cartService.addToCart(
                request.getFoodItemId(),
                request.getQuantity(),
                userDetails.getUsername()
        );
        return ResponseEntity.ok(new CartItemDTO(cartItem));
    }

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CartResponseDTO> getCart(@AuthenticationPrincipal UserDetails userDetails) {
        List<CartItem> cartItems = cartService.getCart(userDetails.getUsername());
        return ResponseEntity.ok(new CartResponseDTO(cartItems));
    }

    @PutMapping("/items/{foodItemId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CartItemDTO> updateCartItem(@PathVariable Long foodItemId,
                                                      @RequestBody CartItemRequest request,
                                                      @AuthenticationPrincipal UserDetails userDetails) {
        CartItem cartItem = cartService.updateCartItem(
                foodItemId,
                request.getQuantity(),
                userDetails.getUsername()
        );
        return ResponseEntity.ok(new CartItemDTO(cartItem));
    }

    @DeleteMapping("/items/{foodItemId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> removeCartItem(@PathVariable Long foodItemId,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        cartService.removeCartItem(foodItemId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal UserDetails userDetails) {
        cartService.clearCart(userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    // DTO for cart item request
    public static class CartItemRequest {
        private Long foodItemId;
        private Integer quantity;

        public Long getFoodItemId() {
            return foodItemId;
        }

        public void setFoodItemId(Long foodItemId) {
            this.foodItemId = foodItemId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }

    // DTO for cart item response
    public static class CartItemDTO {
        private Long cartItemId;
        private Long foodItemId;
        private String foodItemName;
        private String foodItemDescription;
        private Double foodItemPrice;
        private String foodItemImageUrl;
        private Long restaurantId;
        private String restaurantName;
        private Integer quantity;

        public CartItemDTO(CartItem cartItem) {
            this.cartItemId = cartItem.getCartItemId();
            this.foodItemId = cartItem.getFoodItem().getFoodItemId();
            this.foodItemName = cartItem.getFoodItem().getName();
            this.foodItemDescription = cartItem.getFoodItem().getDescription();
            this.foodItemPrice = cartItem.getFoodItem().getPrice();
            this.foodItemImageUrl = cartItem.getFoodItem().getImageUrl();
            this.restaurantId = cartItem.getFoodItem().getRestaurant().getRestaurantId();
            this.restaurantName = cartItem.getFoodItem().getRestaurant().getName();
            this.quantity = cartItem.getQuantity();
        }

        // Getters and Setters
        public Long getCartItemId() { return cartItemId; }
        public void setCartItemId(Long cartItemId) { this.cartItemId = cartItemId; }
        public Long getFoodItemId() { return foodItemId; }
        public void setFoodItemId(Long foodItemId) { this.foodItemId = foodItemId; }
        public String getFoodItemName() { return foodItemName; }
        public void setFoodItemName(String foodItemName) { this.foodItemName = foodItemName; }
        public String getFoodItemDescription() { return foodItemDescription; }
        public void setFoodItemDescription(String foodItemDescription) { this.foodItemDescription = foodItemDescription; }
        public Double getFoodItemPrice() { return foodItemPrice; }
        public void setFoodItemPrice(Double foodItemPrice) { this.foodItemPrice = foodItemPrice; }
        public String getFoodItemImageUrl() { return foodItemImageUrl; }
        public void setFoodItemImageUrl(String foodItemImageUrl) { this.foodItemImageUrl = foodItemImageUrl; }
        public Long getRestaurantId() { return restaurantId; }
        public void setRestaurantId(Long restaurantId) { this.restaurantId = restaurantId; }
        public String getRestaurantName() { return restaurantName; }
        public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }

    // DTO for cart response
    public static class CartResponseDTO {
        private List<CartItemDTO> items;
        private Double totalPrice;

        public CartResponseDTO(List<CartItem> cartItems) {
            this.items = cartItems.stream()
                    .map(CartItemDTO::new)
                    .collect(Collectors.toList());
            this.totalPrice = cartItems.stream()
                    .mapToDouble(item -> item.getFoodItem().getPrice() * item.getQuantity())
                    .sum();
        }

        // Getters and Setters
        public List<CartItemDTO> getItems() { return items; }
        public void setItems(List<CartItemDTO> items) { this.items = items; }
        public Double getTotalPrice() { return totalPrice; }
        public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
    }
}