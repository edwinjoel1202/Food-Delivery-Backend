package com.terminators.FoodDelivery.controller;

import com.terminators.FoodDelivery.model.Order;
import com.terminators.FoodDelivery.model.OrderItem;
import com.terminators.FoodDelivery.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// Removed OrderRequest DTO as it's no longer needed for direct order creation
// Kept OrderResponseDTO, OrderItemDTO, and StatusUpdateRequest as they are still relevant

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // Removed @Autowired private OrderItem orderItem; as it’s not needed

    // Removed POST /api/orders endpoint to disable direct order creation
    // Only allow orders from cart
    @PostMapping("/from-cart")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderResponseDTO> createOrderFromCart(@AuthenticationPrincipal UserDetails userDetails) {
        Order order = orderService.createOrderFromCart(userDetails.getUsername());
        return ResponseEntity.ok(new OrderResponseDTO(order));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<OrderResponseDTO>> getMyOrders(@AuthenticationPrincipal UserDetails userDetails) {
        List<Order> orders = orderService.getCustomerOrders(userDetails.getUsername());
        List<OrderResponseDTO> orderDTOs = orders.stream()
                .map(OrderResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orderDTOs);
    }

    @GetMapping("/restaurant/{restaurantId}")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<List<OrderResponseDTO>> getRestaurantOrders(@PathVariable Long restaurantId,
                                                                      @AuthenticationPrincipal UserDetails userDetails) {
        List<Order> orders = orderService.getRestaurantOrders(restaurantId, userDetails.getUsername());
        List<OrderResponseDTO> orderDTOs = orders.stream()
                .map(OrderResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orderDTOs);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(@PathVariable("id") Long orderId,
                                                              @RequestBody StatusUpdateRequest statusUpdateRequest,
                                                              @AuthenticationPrincipal UserDetails userDetails) {
        Order order = orderService.updateOrderStatus(orderId, statusUpdateRequest.getStatus(), userDetails.getUsername());
        return ResponseEntity.ok(new OrderResponseDTO(order));
    }

    // DTO for status update
    public static class StatusUpdateRequest {
        private Order.OrderStatus status;

        public Order.OrderStatus getStatus() { return status; }
        public void setStatus(Order.OrderStatus status) { this.status = status; }
    }

    // DTO for order response
    public static class OrderResponseDTO {
        private Long orderId;
        private Long customerId;
        private String customerName;
        private Long restaurantId;
        private String restaurantName;
        private Double totalPrice;
        private Order.OrderStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<OrderItemDTO> orderItems;

        public OrderResponseDTO(Order order) {
            this.orderId = order.getOrderId();
            this.customerId = order.getCustomer().getUserId();
            this.customerName = order.getCustomer().getName();
            this.restaurantId = order.getRestaurant().getRestaurantId();
            this.restaurantName = order.getRestaurant().getName();
            this.totalPrice = order.getTotalPrice();
            this.status = order.getStatus();
            this.createdAt = order.getCreatedAt();
            this.updatedAt = order.getUpdatedAt();
            this.orderItems = order.getOrderItems().stream()
                    .map(OrderItemDTO::new)
                    .collect(Collectors.toList());
        }

        // Getters and Setters
        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }
        public Long getCustomerId() { return customerId; }
        public void setCustomerId(Long customerId) { this.customerId = customerId; }
        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }
        public Long getRestaurantId() { return restaurantId; }
        public void setRestaurantId(Long restaurantId) { this.restaurantId = restaurantId; }
        public String getRestaurantName() { return restaurantName; }
        public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }
        public Double getTotalPrice() { return totalPrice; }
        public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
        public Order.OrderStatus getStatus() { return status; }
        public void setStatus(Order.OrderStatus status) { this.status = status; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
        public List<OrderItemDTO> getOrderItems() { return orderItems; }
        public void setOrderItems(List<OrderItemDTO> orderItems) { this.orderItems = orderItems; }
    }

    public static class OrderItemDTO {
        private Long orderItemId;
        private Long foodItemId;
        private String foodItemName;
        private String foodItemDescription;
        private String foodItemImageUrl;
        private Integer quantity;
        private Double unitPrice;

        public OrderItemDTO(OrderItem orderItem) {
            this.orderItemId = orderItem.getOrderItemId();
            this.foodItemId = orderItem.getFoodItem().getFoodItemId();
            this.foodItemName = orderItem.getFoodItem().getName();
            this.foodItemDescription = orderItem.getFoodItem().getDescription();
            this.foodItemImageUrl = orderItem.getFoodItem().getImageUrl();
            this.quantity = orderItem.getQuantity();
            this.unitPrice = orderItem.getUnitPrice();
        }

        // Getters and Setters
        public Long getOrderItemId() { return orderItemId; }
        public void setOrderItemId(Long orderItemId) { this.orderItemId = orderItemId; }
        public Long getFoodItemId() { return foodItemId; }
        public void setFoodItemId(Long foodItemId) { this.foodItemId = foodItemId; }
        public String getFoodItemName() { return foodItemName; }
        public void setFoodItemName(String foodItemName) { this.foodItemName = foodItemName; }
        public String getFoodItemDescription() { return foodItemDescription; }
        public void setFoodItemDescription(String foodItemDescription) { this.foodItemDescription = foodItemDescription; }
        public String getFoodItemImageUrl() { return foodItemImageUrl; }
        public void setFoodItemImageUrl(String foodItemImageUrl) { this.foodItemImageUrl = foodItemImageUrl; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public Double getUnitPrice() { return unitPrice; }
        public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }
    }
}