package com.shoppingCartBackend.shoppingCartBackend.service.notification;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Async("taskExecutor")
    public void sendOrderConfirmation(Long orderId) {

System.out.println("Started Notification for Order: " + orderId + " | Thread: " + Thread.currentThread().getName());
        try {
            Thread.sleep(5000); // simulate sending email
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

System.out.println("Finished Notification for Order: " + orderId + " | Thread: " + Thread.currentThread().getName());    }
}