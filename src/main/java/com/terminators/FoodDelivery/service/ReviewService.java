package com.terminators.FoodDelivery.service;

import com.terminators.FoodDelivery.model.FoodItem;
import com.terminators.FoodDelivery.model.Order;
import com.terminators.FoodDelivery.model.OrderItem;
import com.terminators.FoodDelivery.model.Review;
import com.terminators.FoodDelivery.model.User;
import com.terminators.FoodDelivery.repository.FoodItemRepository;
import com.terminators.FoodDelivery.repository.OrderRepository;
import com.terminators.FoodDelivery.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private FoodItemRepository foodItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserService userService;

    @Transactional
    public Review createReview(Long foodItemId, Integer rating, String comment, String customerEmail) {
        User customer = userService.getUserByEmail(customerEmail);
        if (customer == null || !"CUSTOMER".equals(customer.getRole())) {
            throw new RuntimeException("Only customers can submit reviews");
        }

        FoodItem foodItem = foodItemRepository.findById(foodItemId)
                .orElseThrow(() -> new RuntimeException("Food item not found with id: " + foodItemId));

        // Check if the customer has ordered and received this food item
        boolean hasOrdered = false;
        List<Order> orders = orderRepository.findByCustomerUserId(customer.getUserId());
        for (Order order : orders) {
            if (order.getStatus() != Order.OrderStatus.DELIVERED) {
                continue;
            }
            for (OrderItem orderItem : order.getOrderItems()) {
                if (orderItem.getFoodItem().getFoodItemId().equals(foodItemId)) {
                    hasOrdered = true;
                    break;
                }
            }
            if (hasOrdered) break;
        }
        if (!hasOrdered) {
            throw new RuntimeException("You can only review food items from delivered orders");
        }

        // Check if the customer has already reviewed this food item
        if (reviewRepository.findByFoodItemFoodItemIdAndCustomerUserId(foodItemId, customer.getUserId()).isPresent()) {
            throw new RuntimeException("You have already reviewed this food item");
        }

        // Validate rating
        if (rating < 1 || rating > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }

        Review review = new Review();
        review.setFoodItem(foodItem);
        review.setCustomer(customer);
        review.setRating(rating);
        review.setComment(comment);
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());

        return reviewRepository.save(review);
    }

    public List<Review> getReviewsByFoodItem(Long foodItemId) {
        return reviewRepository.findByFoodItemFoodItemId(foodItemId);
    }
}