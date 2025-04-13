package com.terminators.FoodDelivery.service;

import com.terminators.FoodDelivery.model.FoodItem;
import com.terminators.FoodDelivery.model.Order;
import com.terminators.FoodDelivery.model.OrderItem;
import com.terminators.FoodDelivery.model.User;
import com.terminators.FoodDelivery.repository.FoodItemRepository;
import com.terminators.FoodDelivery.repository.OrderItemRepository;
import com.terminators.FoodDelivery.repository.OrderRepository;
import com.terminators.FoodDelivery.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private FoodItemRepository foodItemRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private UserService userService;

    @Transactional
    public Order createOrder(Long restaurantId, Map<Long, Integer> foodItems, String customerEmail) {
        // Fetch and validate customer
        User customer = userService.getUserByEmail(customerEmail);
        if (customer == null) {
            throw new RuntimeException("User not found with email: " + customerEmail);
        }
        if (!"CUSTOMER".equals(customer.getRole())) {
            throw new RuntimeException("Only customers can place orders");
        }
        System.out.println("Customer fetched: ID=" + customer.getUserId() + ", Email=" + customer.getEmail()); // Debug

        // Fetch and validate restaurant
        var restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + restaurantId));

        // Initialize order
        Order order = new Order();
        order.setCustomer(customer);
        order.setRestaurant(restaurant);
        order.setStatus(Order.OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        // Process food items
        double totalPrice = 0.0;
        List<OrderItem> orderItems = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : foodItems.entrySet()) {
            Long foodItemId = entry.getKey();
            Integer quantity = entry.getValue();

            if (quantity <= 0) {
                throw new RuntimeException("Quantity must be positive for food item ID: " + foodItemId);
            }

            FoodItem foodItem = foodItemRepository.findById(foodItemId)
                    .orElseThrow(() -> new RuntimeException("Food item not found with id: " + foodItemId));

            if (!foodItem.getRestaurant().getRestaurantId().equals(restaurantId)) {
                throw new RuntimeException("Food item ID " + foodItemId + " does not belong to restaurant ID " + restaurantId);
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setFoodItem(foodItem);
            orderItem.setQuantity(quantity);
            orderItem.setUnitPrice(foodItem.getPrice());
            order.addOrderItem(orderItem);

            totalPrice += foodItem.getPrice() * quantity;
        }

        order.setTotalPrice(totalPrice);
        System.out.println("Order before save: CustomerID=" + (order.getCustomer() != null ? order.getCustomer().getUserId() : "null")); // Debug
        return orderRepository.save(order);
    }

    public List<Order> getCustomerOrders(String customerEmail) {
        User customer = userService.getUserByEmail(customerEmail);
        if (customer == null) {
            throw new RuntimeException("User not found with email: " + customerEmail);
        }
        return orderRepository.findByCustomerUserId(customer.getUserId());
    }

    public List<Order> getRestaurantOrders(Long restaurantId, String ownerEmail) {
        User owner = userService.getUserByEmail(ownerEmail);
        if (owner == null) {
            throw new RuntimeException("User not found with email: " + ownerEmail);
        }
        var restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + restaurantId));
        if (!restaurant.getOwner().getUserId().equals(owner.getUserId())) {
            throw new RuntimeException("You can only view orders for your own restaurants");
        }
        return orderRepository.findByRestaurantRestaurantId(restaurantId);
    }

    public Order updateOrderStatus(Long orderId, Order.OrderStatus newStatus, String ownerEmail) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        User owner = userService.getUserByEmail(ownerEmail);
        if (owner == null) {
            throw new RuntimeException("User not found with email: " + ownerEmail);
        }
        if (!order.getRestaurant().getOwner().getUserId().equals(owner.getUserId())) {
            throw new RuntimeException("You can only update orders for your own restaurants");
        }

        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }
}