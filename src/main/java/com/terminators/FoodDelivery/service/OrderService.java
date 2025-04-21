package com.terminators.FoodDelivery.service;

import com.terminators.FoodDelivery.model.*;
import com.terminators.FoodDelivery.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Autowired
    private CartService cartService;

    @Autowired
    private DeliveryService deliveryService;

    @Transactional
    public Order createOrderFromCart(String customerEmail) {
        List<CartItem> cartItems = cartService.getCart(customerEmail);
        if (cartItems.isEmpty()) throw new RuntimeException("Cannot create order: Cart is empty");

        Long restaurantId = cartItems.get(0).getFoodItem().getRestaurant().getRestaurantId();
        var restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + restaurantId));
        if (!"ACTIVE".equals(restaurant.getStatus())) throw new RuntimeException("Restaurant is not accepting orders");

        User customer = userService.getUserByEmail(customerEmail);
        if (customer == null) throw new RuntimeException("User not found with email: " + customerEmail);
        if (!"CUSTOMER".equals(customer.getRole())) throw new RuntimeException("Only customers can place orders");

        Order order = new Order();
        order.setCustomer(customer);
        order.setRestaurant(restaurant);
        order.setStatus(Order.OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        double totalPrice = 0.0;
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            FoodItem foodItem = cartItem.getFoodItem();
            Integer quantity = cartItem.getQuantity();
            if (quantity <= 0) throw new RuntimeException("Invalid quantity for food item ID: " + foodItem.getFoodItemId());

            OrderItem orderItem = new OrderItem();
            orderItem.setFoodItem(foodItem);
            orderItem.setQuantity(quantity);
            orderItem.setUnitPrice(foodItem.getPrice());
            order.addOrderItem(orderItem);
            totalPrice += foodItem.getPrice() * quantity;
        }

        order.setTotalPrice(totalPrice);
        Order savedOrder = orderRepository.save(order);

        String deliveryAddress = customer.getAddress();
        if (deliveryAddress == null || deliveryAddress.trim().isEmpty()) throw new RuntimeException("Delivery address is required");
        deliveryService.createDelivery(savedOrder.getOrderId(), deliveryAddress, customerEmail);

        cartService.clearCart(customerEmail);
        return savedOrder;
    }

    public List<Order> getCustomerOrders(String customerEmail) {
        User customer = userService.getUserByEmail(customerEmail);
        if (customer == null) throw new RuntimeException("User not found with email: " + customerEmail);
        return orderRepository.findByCustomerUserId(customer.getUserId());
    }

    public List<Order> getRestaurantOrders(Long restaurantId, String ownerEmail) {
        User owner = userService.getUserByEmail(ownerEmail);
        if (owner == null) throw new RuntimeException("User not found with email: " + ownerEmail);
        var restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + restaurantId));
        if (!restaurant.getOwner().getUserId().equals(owner.getUserId())) throw new RuntimeException("You can only view orders for your own restaurants");
        return orderRepository.findByRestaurantRestaurantId(restaurantId);
    }

    public Order updateOrderStatus(Long orderId, Order.OrderStatus newStatus, String ownerEmail) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        User owner = userService.getUserByEmail(ownerEmail);
        if (owner == null) throw new RuntimeException("User not found with email: " + ownerEmail);
        if (!order.getRestaurant().getOwner().getUserId().equals(owner.getUserId())) throw new RuntimeException("You can only update orders for your own restaurants");
        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
    }
}