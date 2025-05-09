package com.terminators.FoodDelivery.controller;

import com.terminators.FoodDelivery.model.Delivery;
import com.terminators.FoodDelivery.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController {

    @Autowired
    private DeliveryService deliveryService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<DeliveryDTO> createDelivery(@RequestBody DeliveryRequest request,
                                                      @AuthenticationPrincipal UserDetails userDetails) {
        Delivery delivery = deliveryService.createDelivery(
                request.getOrderId(),
                request.getDeliveryAddress(),
                userDetails.getUsername()
        );
        return ResponseEntity.ok(new DeliveryDTO(delivery));
    }

    @PutMapping("/{id}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeliveryDTO> assignDeliveryPerson(@PathVariable Long id,
                                                            @RequestBody AssignDeliveryRequest request,
                                                            @AuthenticationPrincipal UserDetails userDetails) {
        Delivery delivery = deliveryService.assignDeliveryPerson(id, request.getDeliveryPersonEmail(), userDetails.getUsername());
        return ResponseEntity.ok(new DeliveryDTO(delivery));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeliveryDTO> updateStatus(@PathVariable Long id,
                                                    @RequestBody StatusUpdateRequest request,
                                                    @AuthenticationPrincipal UserDetails userDetails) {
        Delivery delivery = deliveryService.updateStatus(id, request.getStatus(), userDetails.getUsername());
        return ResponseEntity.ok(new DeliveryDTO(delivery));
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<DeliveryDTO> getDelivery(@PathVariable Long orderId,
                                                   @AuthenticationPrincipal UserDetails userDetails) {
        Delivery delivery = deliveryService.getDelivery(orderId);
        return ResponseEntity.ok(new DeliveryDTO(delivery));
    }

    // DTOs
    public static class DeliveryRequest {
        private Long orderId;
        private String deliveryAddress;

        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }
        public String getDeliveryAddress() { return deliveryAddress; }
        public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    }

    public static class AssignDeliveryRequest {
        private String deliveryPersonEmail;

        public String getDeliveryPersonEmail() { return deliveryPersonEmail; }
        public void setDeliveryPersonEmail(String deliveryPersonEmail) { this.deliveryPersonEmail = deliveryPersonEmail; }
    }

    public static class StatusUpdateRequest {
        private Delivery.DeliveryStatus status;

        public Delivery.DeliveryStatus getStatus() { return status; }
        public void setStatus(Delivery.DeliveryStatus status) { this.status = status; }
    }

    public static class DeliveryDTO {
        private Long deliveryId;
        private Long orderId;
        private Long deliveryPersonId;
        private String deliveryAddress;
        private Delivery.DeliveryStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<DeliveryStatusHistoryDTO> statusHistory;

        public DeliveryDTO(Delivery delivery) {
            this.deliveryId = delivery.getDeliveryId();
            this.orderId = (delivery.getOrder() != null) ? delivery.getOrder().getOrderId() : null;
            this.deliveryPersonId = (delivery.getDeliveryPerson() != null) ? delivery.getDeliveryPerson().getUserId() : null;
            this.deliveryAddress = delivery.getDeliveryAddress();
            this.status = delivery.getStatus();
            this.createdAt = delivery.getCreatedAt();
            this.updatedAt = delivery.getUpdatedAt();
            this.statusHistory = delivery.getStatusHistory().stream()
                    .map(history -> new DeliveryStatusHistoryDTO(history.getStatus(), history.getTimestamp()))
                    .collect(Collectors.toList());
        }

        // Getters and Setters
        public Long getDeliveryId() { return deliveryId; }
        public void setDeliveryId(Long deliveryId) { this.deliveryId = deliveryId; }
        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }
        public Long getDeliveryPersonId() { return deliveryPersonId; }
        public void setDeliveryPersonId(Long deliveryPersonId) { this.deliveryPersonId = deliveryPersonId; }
        public String getDeliveryAddress() { return deliveryAddress; }
        public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
        public Delivery.DeliveryStatus getStatus() { return status; }
        public void setStatus(Delivery.DeliveryStatus status) { this.status = status; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
        public List<DeliveryStatusHistoryDTO> getStatusHistory() { return statusHistory; }
        public void setStatusHistory(List<DeliveryStatusHistoryDTO> statusHistory) { this.statusHistory = statusHistory; }
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