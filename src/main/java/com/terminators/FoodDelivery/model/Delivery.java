package com.terminators.FoodDelivery.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "deliveries")
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deliveryId;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "delivery_person_id")
    private User deliveryPerson;

    @Column(nullable = false)
    private String deliveryAddress;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status = DeliveryStatus.PENDING;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeliveryStatusHistory> statusHistory = new ArrayList<>();

    public enum DeliveryStatus {
        PENDING, ASSIGNED, IN_TRANSIT, DELIVERED, CANCELLED
    }

    // Getters and Setters
    public Long getDeliveryId() { return deliveryId; }
    public void setDeliveryId(Long deliveryId) { this.deliveryId = deliveryId; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    public User getDeliveryPerson() { return deliveryPerson; }
    public void setDeliveryPerson(User deliveryPerson) { this.deliveryPerson = deliveryPerson; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    public DeliveryStatus getStatus() { return status; }
    public void setStatus(DeliveryStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public List<DeliveryStatusHistory> getStatusHistory() { return statusHistory; }
    public void setStatusHistory(List<DeliveryStatusHistory> statusHistory) { this.statusHistory = statusHistory; }

    // Helper method to add status history
    public void addStatusHistory(DeliveryStatus status) {
        DeliveryStatusHistory history = new DeliveryStatusHistory();
        history.setDelivery(this);
        history.setStatus(status);
        history.setTimestamp(LocalDateTime.now());
        statusHistory.add(history);
    }
}