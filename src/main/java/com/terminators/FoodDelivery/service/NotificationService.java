package com.terminators.FoodDelivery.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String toEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    public void sendOrderStatusNotification(String toEmail, String customerName, String orderId, String status) {
        String body = String.format(
                "Dear %s,\n\nYour order #%s has been updated to status: %s.\n\nThank you for choosing FoodDelivery!\n\nBest regards,\nFoodDelivery Team",
                customerName, orderId, status
        );
        sendEmail(toEmail, "Order Status Update - Order #" + orderId, body);
    }

    public void sendDeliveryStatusNotification(String toEmail, String customerName, String orderId, String deliveryStatus) {
        String body = String.format(
                "Dear %s,\n\nThe delivery for your order #%s has been updated to status: %s.\n\nThank you for choosing FoodDelivery!\n\nBest regards,\nFoodDelivery Team",
                customerName, orderId, deliveryStatus
        );
        sendEmail(toEmail, "Delivery Status Update - Order #" + orderId, body);
    }

    public void sendOrderStatusNotificationToRestaurant(String toEmail, String restaurantName, String orderId, String status) {
        String body = String.format(
                "Dear %s,\n\nOrder #%s has been updated to status: %s.\nPlease check your dashboard for details.\n\nBest regards,\nFoodDelivery Team",
                restaurantName, orderId, status
        );
        sendEmail(toEmail, "New Order Status Update - Order #" + orderId, body);
    }
}