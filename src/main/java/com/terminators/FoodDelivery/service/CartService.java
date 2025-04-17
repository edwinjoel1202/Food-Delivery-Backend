package com.terminators.FoodDelivery.service;

import com.terminators.FoodDelivery.model.CartItem;
import com.terminators.FoodDelivery.model.FoodItem;
import com.terminators.FoodDelivery.model.User;
import com.terminators.FoodDelivery.repository.CartItemRepository;
import com.terminators.FoodDelivery.repository.FoodItemRepository;
import com.terminators.FoodDelivery.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FoodItemRepository foodItemRepository;

    @Transactional
    public CartItem addToCart(Long foodItemId, Integer quantity, String customerEmail) {
        // Validate customer
        User customer = userRepository.findByEmail(customerEmail);
        if (customer == null) {
            throw new RuntimeException("User not found with email: " + customerEmail);
        }
        if (!"CUSTOMER".equals(customer.getRole())) {
            throw new RuntimeException("Only customers can manage carts");
        }

        // Validate food item
        FoodItem foodItem = foodItemRepository.findById(foodItemId)
                .orElseThrow(() -> new RuntimeException("Food item not found with id: " + foodItemId));

        // Validate quantity
        if (quantity <= 0) {
            throw new RuntimeException("Quantity must be positive");
        }

        // Check if item already exists in cart
        Optional<CartItem> existingCartItem = cartItemRepository.findByUserUserIdAndFoodItemFoodItemId(customer.getUserId(), foodItemId);
        if (existingCartItem.isPresent()) {
            // Update quantity
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setUpdatedAt(LocalDateTime.now());
            return cartItemRepository.save(cartItem);
        } else {
            // Validate restaurant consistency
            List<CartItem> cartItems = cartItemRepository.findByUserUserId(customer.getUserId());
            if (!cartItems.isEmpty()) {
                Long cartRestaurantId = cartItems.get(0).getFoodItem().getRestaurant().getRestaurantId();
                if (!foodItem.getRestaurant().getRestaurantId().equals(cartRestaurantId)) {
                    throw new RuntimeException("All cart items must belong to the same restaurant");
                }
            }

            // Add new item
            CartItem cartItem = new CartItem();
            cartItem.setUser(customer);
            cartItem.setFoodItem(foodItem);
            cartItem.setQuantity(quantity);
            cartItem.setCreatedAt(LocalDateTime.now());
            cartItem.setUpdatedAt(LocalDateTime.now());
            return cartItemRepository.save(cartItem);
        }
    }

    public List<CartItem> getCart(String customerEmail) {
        // Use findByEmail directly since it returns User, not Optional
        User customer = userRepository.findByEmail(customerEmail);
        if (customer == null) {
            throw new RuntimeException("User not found with email: " + customerEmail);
        }
        return cartItemRepository.findByUserUserId(customer.getUserId());
    }

    @Transactional
    public CartItem updateCartItem(Long foodItemId, Integer quantity, String customerEmail) {
        User customer = userRepository.findByEmail(customerEmail);
        if (customer == null) {
            throw new RuntimeException("User not found with email: " + customerEmail);
        }
        if (!"CUSTOMER".equals(customer.getRole())) {
            throw new RuntimeException("Only customers can manage carts");
        }

        if (quantity <= 0) {
            throw new RuntimeException("Quantity must be positive");
        }

        CartItem cartItem = cartItemRepository.findByUserUserIdAndFoodItemFoodItemId(customer.getUserId(), foodItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found for food item ID: " + foodItemId));

        cartItem.setQuantity(quantity);
        cartItem.setUpdatedAt(LocalDateTime.now());
        return cartItemRepository.save(cartItem);
    }

    @Transactional
    public void removeCartItem(Long foodItemId, String customerEmail) {
        User customer = userRepository.findByEmail(customerEmail);
        if (customer == null) {
            throw new RuntimeException("User not found with email: " + customerEmail);
        }
        if (!"CUSTOMER".equals(customer.getRole())) {
            throw new RuntimeException("Only customers can manage carts");
        }

        CartItem cartItem = cartItemRepository.findByUserUserIdAndFoodItemFoodItemId(customer.getUserId(), foodItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found for food item ID: " + foodItemId));

        cartItemRepository.delete(cartItem);
    }

    @Transactional
    public void clearCart(String customerEmail) {
        User customer = userRepository.findByEmail(customerEmail);
        if (customer == null) {
            throw new RuntimeException("User not found with email: " + customerEmail);
        }
        cartItemRepository.deleteByUserUserId(customer.getUserId());
    }
}