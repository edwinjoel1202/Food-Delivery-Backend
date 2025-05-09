package com.terminators.FoodDelivery.service;

import com.terminators.FoodDelivery.model.Delivery;
import com.terminators.FoodDelivery.model.Order;
import com.terminators.FoodDelivery.model.User;
import com.terminators.FoodDelivery.repository.DeliveryRepository;
import com.terminators.FoodDelivery.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DeliveryService {
    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @Transactional
    public Delivery createDelivery(Long orderId, String deliveryAddress, String customerEmail) {
        User customer = userService.getUserByEmail(customerEmail);
        if (customer == null) throw new RuntimeException("User not found with email: " + customerEmail);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        Delivery delivery = new Delivery();
        delivery.setOrder(order);
        delivery.setDeliveryAddress(deliveryAddress);
        delivery.addStatusHistory(Delivery.DeliveryStatus.PENDING);
        Delivery savedDelivery = deliveryRepository.save(delivery);

        // Send notification to customer
        notificationService.sendDeliveryStatusNotification(
                customer.getEmail(),
                customer.getName(),
                orderId.toString(),
                savedDelivery.getStatus().toString()
        );

        return savedDelivery;
    }

    @Transactional
    public Delivery assignDeliveryPerson(Long deliveryId, String deliveryPersonEmail, String adminEmail) {
        User admin = userService.getUserByEmail(adminEmail);
        if (admin == null || !"ADMIN".equals(admin.getRole())) throw new RuntimeException("Unauthorized access: Admin required");
        User deliveryPerson = userService.getUserByEmail(deliveryPersonEmail);
        if (deliveryPerson == null || !"DELIVERY_PERSON".equals(deliveryPerson.getRole())) throw new RuntimeException("Invalid delivery person");

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found with id: " + deliveryId));
        delivery.setDeliveryPerson(deliveryPerson);
        delivery.setStatus(Delivery.DeliveryStatus.ASSIGNED);
        delivery.setUpdatedAt(LocalDateTime.now());
        delivery.addStatusHistory(Delivery.DeliveryStatus.ASSIGNED);
        Delivery updatedDelivery = deliveryRepository.save(delivery);

        // Send notification to customer
        Order order = updatedDelivery.getOrder();
        notificationService.sendDeliveryStatusNotification(
                order.getCustomer().getEmail(),
                order.getCustomer().getName(),
                order.getOrderId().toString(),
                updatedDelivery.getStatus().toString()
        );

        return updatedDelivery;
    }

    @Transactional
    public Delivery updateStatus(Long deliveryId, Delivery.DeliveryStatus status, String updaterEmail) {
        User updater = userService.getUserByEmail(updaterEmail);
        if (updater == null || !("ADMIN".equals(updater.getRole()) || "RESTAURANT_OWNER".equals(updater.getRole())))
            throw new RuntimeException("Unauthorized access: Admin or restaurant owner required");

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found with id: " + deliveryId));
        delivery.setStatus(status);
        delivery.setUpdatedAt(LocalDateTime.now());
        delivery.addStatusHistory(status);
        Delivery updatedDelivery = deliveryRepository.save(delivery);

        // Send notification to customer
        Order order = updatedDelivery.getOrder();
        notificationService.sendDeliveryStatusNotification(
                order.getCustomer().getEmail(),
                order.getCustomer().getName(),
                order.getOrderId().toString(),
                updatedDelivery.getStatus().toString()
        );

        return updatedDelivery;
    }

    @Transactional
    public Delivery updateStatusByDeliveryPerson(Long deliveryId, Delivery.DeliveryStatus status, String deliveryPersonEmail) {
        User deliveryPerson = userService.getUserByEmail(deliveryPersonEmail);
        if (deliveryPerson == null || !"DELIVERY_PERSON".equals(deliveryPerson.getRole()))
            throw new RuntimeException("Unauthorized access: Delivery person required");

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found with id: " + deliveryId));
        if (delivery.getDeliveryPerson() == null || !delivery.getDeliveryPerson().getUserId().equals(deliveryPerson.getUserId()))
            throw new RuntimeException("You can only update your own assigned deliveries");

        // Validate status transition
        if (status == Delivery.DeliveryStatus.PENDING || status == Delivery.DeliveryStatus.ASSIGNED)
            throw new RuntimeException("Delivery person can only update status to IN_TRANSIT, DELIVERED, or CANCELLED");
        if (delivery.getStatus() == Delivery.DeliveryStatus.DELIVERED || delivery.getStatus() == Delivery.DeliveryStatus.CANCELLED)
            throw new RuntimeException("Delivery is already completed or cancelled");

        delivery.setStatus(status);
        delivery.setUpdatedAt(LocalDateTime.now());
        delivery.addStatusHistory(status);
        Delivery updatedDelivery = deliveryRepository.save(delivery);

        // Update order status if delivery is completed
        Order order = updatedDelivery.getOrder();
        if (status == Delivery.DeliveryStatus.DELIVERED) {
            order.setStatus(Order.OrderStatus.DELIVERED);
            order.setUpdatedAt(LocalDateTime.now());
            order.addStatusHistory(Order.OrderStatus.DELIVERED);
            orderRepository.save(order);
        } else if (status == Delivery.DeliveryStatus.CANCELLED) {
            order.setStatus(Order.OrderStatus.CANCELLED);
            order.setUpdatedAt(LocalDateTime.now());
            order.addStatusHistory(Order.OrderStatus.CANCELLED);
            orderRepository.save(order);
        }

        // Send notification to customer
        notificationService.sendDeliveryStatusNotification(
                order.getCustomer().getEmail(),
                order.getCustomer().getName(),
                order.getOrderId().toString(),
                updatedDelivery.getStatus().toString()
        );

        return updatedDelivery;
    }

    public List<Delivery> getAssignedDeliveries(String deliveryPersonEmail) {
        User deliveryPerson = userService.getUserByEmail(deliveryPersonEmail);
        if (deliveryPerson == null) throw new RuntimeException("User not found with email: " + deliveryPersonEmail);
        return deliveryRepository.findByDeliveryPersonUserId(deliveryPerson.getUserId());
    }

    public Delivery getDelivery(Long orderId) {
        return deliveryRepository.findByOrderOrderId(orderId);
    }
}