package com.terminators.FoodDelivery.controller;

import com.terminators.FoodDelivery.model.Order;
import com.terminators.FoodDelivery.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest orderRequest,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        Order order = orderService.createOrder(
                orderRequest.getRestaurantId(),
                orderRequest.getFoodItems(),
                userDetails.getUsername()
        );
        return ResponseEntity.ok(order);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<Order>> getMyOrders(@AuthenticationPrincipal UserDetails userDetails) {
        List<Order> orders = orderService.getCustomerOrders(userDetails.getUsername());
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/restaurant/{restaurantId}")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<List<Order>> getRestaurantOrders(@PathVariable Long restaurantId,
                                                           @AuthenticationPrincipal UserDetails userDetails) {
        List<Order> orders = orderService.getRestaurantOrders(restaurantId, userDetails.getUsername());
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable("id") Long orderId,
                                                   @RequestBody StatusUpdateRequest statusUpdateRequest,
                                                   @AuthenticationPrincipal UserDetails userDetails) {
        Order order = orderService.updateOrderStatus(orderId, statusUpdateRequest.getStatus(), userDetails.getUsername());
        return ResponseEntity.ok(order);
    }

    // DTO for order creation
    public static class OrderRequest {
        private Long restaurantId;
        private Map<Long, Integer> foodItems; // foodItemId -> quantity

        public Long getRestaurantId() { return restaurantId; }
        public void setRestaurantId(Long restaurantId) { this.restaurantId = restaurantId; }
        public Map<Long, Integer> getFoodItems() { return foodItems; }
        public void setFoodItems(Map<Long, Integer> foodItems) { this.foodItems = foodItems; }
    }

    // DTO for status update
    public static class StatusUpdateRequest {
        private Order.OrderStatus status;

        public Order.OrderStatus getStatus() { return status; }
        public void setStatus(Order.OrderStatus status) { this.status = status; }
    }
}