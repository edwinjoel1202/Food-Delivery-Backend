package com.terminators.FoodDelivery.service;

import com.terminators.FoodDelivery.model.Delivery;
import com.terminators.FoodDelivery.model.Order;
import com.terminators.FoodDelivery.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderTrackingService {

    @Autowired
    private OrderService orderService;

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private UserService userService;

    public OrderTrackingDTO getOrderTracking(Long orderId, String customerEmail) {
        User customer = userService.getUserByEmail(customerEmail);
        if (customer == null) throw new RuntimeException("User not found with email: " + customerEmail);

        Order order = orderService.getOrder(orderId);
        if (!order.getCustomer().getUserId().equals(customer.getUserId())) {
            throw new RuntimeException("You can only track your own orders");
        }

        Delivery delivery = deliveryService.getDelivery(orderId);
        return new OrderTrackingDTO(order, delivery);
    }

    public static class OrderTrackingDTO {
        private Long orderId;
        private Order.OrderStatus currentOrderStatus;
        private List<OrderStatusHistoryDTO> orderStatusHistory;
        private Delivery.DeliveryStatus currentDeliveryStatus;
        private List<DeliveryStatusHistoryDTO> deliveryStatusHistory;

        public OrderTrackingDTO(Order order, Delivery delivery) {
            this.orderId = order.getOrderId();
            this.currentOrderStatus = order.getStatus();
            this.orderStatusHistory = order.getStatusHistory().stream()
                    .map(history -> new OrderStatusHistoryDTO(history.getStatus(), history.getTimestamp()))
                    .collect(java.util.stream.Collectors.toList());
            this.currentDeliveryStatus = delivery != null ? delivery.getStatus() : null;
            this.deliveryStatusHistory = delivery != null ? delivery.getStatusHistory().stream()
                    .map(history -> new DeliveryStatusHistoryDTO(history.getStatus(), history.getTimestamp()))
                    .collect(java.util.stream.Collectors.toList()) : new java.util.ArrayList<>();
        }

        // Getters and Setters
        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }
        public Order.OrderStatus getCurrentOrderStatus() { return currentOrderStatus; }
        public void setCurrentOrderStatus(Order.OrderStatus currentOrderStatus) { this.currentOrderStatus = currentOrderStatus; }
        public List<OrderStatusHistoryDTO> getOrderStatusHistory() { return orderStatusHistory; }
        public void setOrderStatusHistory(List<OrderStatusHistoryDTO> orderStatusHistory) { this.orderStatusHistory = orderStatusHistory; }
        public Delivery.DeliveryStatus getCurrentDeliveryStatus() { return currentDeliveryStatus; }
        public void setCurrentDeliveryStatus(Delivery.DeliveryStatus currentDeliveryStatus) { this.currentDeliveryStatus = currentDeliveryStatus; }
        public List<DeliveryStatusHistoryDTO> getDeliveryStatusHistory() { return deliveryStatusHistory; }
        public void setDeliveryStatusHistory(List<DeliveryStatusHistoryDTO> deliveryStatusHistory) { this.deliveryStatusHistory = deliveryStatusHistory; }
    }

    public static class OrderStatusHistoryDTO {
        private Order.OrderStatus status;
        private LocalDateTime timestamp;

        public OrderStatusHistoryDTO(Order.OrderStatus status, LocalDateTime timestamp) {
            this.status = status;
            this.timestamp = timestamp;
        }

        // Getters and Setters
        public Order.OrderStatus getStatus() { return status; }
        public void setStatus(Order.OrderStatus status) { this.status = status; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }

    public static class DeliveryStatusHistoryDTO {
        private Delivery.DeliveryStatus status;
        private LocalDateTime timestamp;

        public DeliveryStatusHistoryDTO(Delivery.DeliveryStatus status, LocalDateTime timestamp) {
            this.status = status;
            this.timestamp = timestamp;
        }

        // Getters and Setters
        public Delivery.DeliveryStatus getStatus() { return status; }
        public void setStatus(Delivery.DeliveryStatus status) { this.status = status; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }
}