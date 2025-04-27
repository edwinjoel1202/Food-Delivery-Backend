package com.terminators.FoodDelivery.controller;

import com.terminators.FoodDelivery.model.Delivery;
import com.terminators.FoodDelivery.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/delivery-person")
public class DeliveryPersonController {

    @Autowired
    private DeliveryService deliveryService;

    @GetMapping("/my-deliveries")
    @PreAuthorize("hasRole('DELIVERY_PERSON')")
    public ResponseEntity<List<DeliveryResponseDTO>> getMyDeliveries(@AuthenticationPrincipal UserDetails userDetails) {
        List<Delivery> deliveries = deliveryService.getAssignedDeliveries(userDetails.getUsername());
        List<DeliveryResponseDTO> deliveryDTOs = deliveries.stream()
                .map(DeliveryResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(deliveryDTOs);
    }

    @PutMapping("/{deliveryId}/status")
    @PreAuthorize("hasRole('DELIVERY_PERSON')")
    public ResponseEntity<DeliveryResponseDTO> updateDeliveryStatus(
            @PathVariable Long deliveryId,
            @RequestBody StatusUpdateRequest statusUpdateRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        Delivery delivery = deliveryService.updateStatusByDeliveryPerson(
                deliveryId,
                statusUpdateRequest.getStatus(),
                userDetails.getUsername()
        );
        return ResponseEntity.ok(new DeliveryResponseDTO(delivery));
    }

    // DTO for status update request
    public static class StatusUpdateRequest {
        private Delivery.DeliveryStatus status;

        public Delivery.DeliveryStatus getStatus() { return status; }
        public void setStatus(Delivery.DeliveryStatus status) { this.status = status; }
    }

    // DTO for delivery response
    public static class DeliveryResponseDTO {
        private Long deliveryId;
        private Long orderId;
        private String customerName;
        private String deliveryAddress;
        private Delivery.DeliveryStatus status;

        public DeliveryResponseDTO(Delivery delivery) {
            this.deliveryId = delivery.getDeliveryId();
            this.orderId = delivery.getOrder().getOrderId();
            this.customerName = delivery.getOrder().getCustomer().getName();
            this.deliveryAddress = delivery.getDeliveryAddress();
            this.status = delivery.getStatus();
        }

        // Getters and Setters
        public Long getDeliveryId() { return deliveryId; }
        public void setDeliveryId(Long deliveryId) { this.deliveryId = deliveryId; }
        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }
        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }
        public String getDeliveryAddress() { return deliveryAddress; }
        public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
        public Delivery.DeliveryStatus getStatus() { return status; }
        public void setStatus(Delivery.DeliveryStatus status) { this.status = status; }
    }
}