package com.terminators.FoodDelivery.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOrderStatusNotification(String toEmail, String customerName, String orderId, String status) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Order Status Update - Order #" + orderId);
        message.setText(String.format(
                "Dear %s,\n\nYour order #%s has been updated to status: %s.\n\nThank you for choosing FoodDelivery!\n\nBest regards,\nFoodDelivery Team",
                customerName, orderId, status
        ));
        mailSender.send(message);
    }

    public void sendDeliveryStatusNotification(String toEmail, String customerName, String orderId, String deliveryStatus) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Delivery Status Update - Order #" + orderId);
        message.setText(String.format(
                "Dear %s,\n\nThe delivery for your order #%s has been updated to status: %s.\n\nThank you for choosing FoodDelivery!\n\nBest regards,\nFoodDelivery Team",
                customerName, orderId, deliveryStatus
        ));
        mailSender.send(message);
    }

    public void sendOrderStatusNotificationToRestaurant(String toEmail, String restaurantName, String orderId, String status) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("New Order Status Update - Order #" + orderId);
        message.setText(String.format(
                "Dear %s,\n\nOrder #%s has been updated to status: %s.\nPlease check your dashboard for details.\n\nBest regards,\nFoodDelivery Team",
                restaurantName, orderId, status
        ));
        mailSender.send(message);
    }
}