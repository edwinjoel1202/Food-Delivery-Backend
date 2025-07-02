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

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private DistanceService distanceService;

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

        // Add initial status to history
        order.addStatusHistory(Order.OrderStatus.PENDING);

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

        // Calculate delivery fee based on coordinates
        Double deliveryFee = distanceService.calculateDeliveryFee(
                customer.getLatitude(),
                customer.getLongitude(),
                restaurant.getLatitude(),
                restaurant.getLongitude()
        );
        order.setDeliveryFee(deliveryFee);

        Order savedOrder = orderRepository.save(order);

        String deliveryAddress = customer.getAddress();
        if (deliveryAddress == null || deliveryAddress.trim().isEmpty()) throw new RuntimeException("Delivery address is required");
        deliveryService.createDelivery(savedOrder.getOrderId(), deliveryAddress, customerEmail);

        // Send notification to customer
        notificationService.sendOrderStatusNotification(
                customer.getEmail(),
                customer.getName(),
                savedOrder.getOrderId().toString(),
                savedOrder.getStatus().toString()
        );

        // Send notification to restaurant owner
        notificationService.sendOrderStatusNotificationToRestaurant(
                restaurant.getOwner().getEmail(),
                restaurant.getName(),
                savedOrder.getOrderId().toString(),
                savedOrder.getStatus().toString()
        );

        cartService.clearCart(customerEmail);
        return savedOrder;
    }

    @Transactional
    public Order cancelOrder(Long orderId, String customerEmail) {
        User customer = userService.getUserByEmail(customerEmail);
        if (customer == null) throw new RuntimeException("User not found with email: " + customerEmail);
        if (!"CUSTOMER".equals(customer.getRole())) throw new RuntimeException("Only customers can cancel orders");

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        if (!order.getCustomer().getUserId().equals(customer.getUserId())) throw new RuntimeException("You can only cancel your own orders");

        if (order.getStatus() != Order.OrderStatus.PENDING) throw new RuntimeException("Order cannot be cancelled at this stage");

        order.setStatus(Order.OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        order.addStatusHistory(Order.OrderStatus.CANCELLED);

        Delivery delivery = deliveryService.getDelivery(orderId);
        if (delivery != null) {
            delivery.setStatus(Delivery.DeliveryStatus.CANCELLED);
            delivery.setUpdatedAt(LocalDateTime.now());
            delivery.addStatusHistory(Delivery.DeliveryStatus.CANCELLED);
        }

        Order updatedOrder = orderRepository.save(order);

        // Send notification to customer
        notificationService.sendOrderStatusNotification(
                customer.getEmail(),
                customer.getName(),
                orderId.toString(),
                updatedOrder.getStatus().toString()
        );

        // Send notification to restaurant owner
        notificationService.sendOrderStatusNotificationToRestaurant(
                order.getRestaurant().getOwner().getEmail(),
                order.getRestaurant().getName(),
                orderId.toString(),
                updatedOrder.getStatus().toString()
        );

        return updatedOrder;
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

    @Transactional
    public Order updateOrderStatus(Long orderId, Order.OrderStatus newStatus, String ownerEmail) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        User owner = userService.getUserByEmail(ownerEmail);
        if (owner == null) throw new RuntimeException("User not found with email: " + ownerEmail);
        if (!order.getRestaurant().getOwner().getUserId().equals(owner.getUserId())) throw new RuntimeException("You can only update orders for your own restaurants");

        if (order.getStatus() == Order.OrderStatus.CANCELLED || order.getStatus() == Order.OrderStatus.DELIVERED) {
            throw new RuntimeException("Cannot update status of a cancelled or delivered order");
        }

        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());
        order.addStatusHistory(newStatus);

        Order updatedOrder = orderRepository.save(order);

        // Send notification to customer
        notificationService.sendOrderStatusNotification(
                order.getCustomer().getEmail(),
                order.getCustomer().getName(),
                orderId.toString(),
                newStatus.toString()
        );

        // Send notification to restaurant owner
        notificationService.sendOrderStatusNotificationToRestaurant(
                owner.getEmail(),
                order.getRestaurant().getName(),
                orderId.toString(),
                newStatus.toString()
        );

        return updatedOrder;
    }

    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
    }
}