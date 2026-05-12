package com.shoppingCartBackend.shoppingCartBackend.data;

import com.shoppingCartBackend.shoppingCartBackend.enums.OrderStatus;
import com.shoppingCartBackend.shoppingCartBackend.model.Order;
import com.shoppingCartBackend.shoppingCartBackend.model.User;
import com.shoppingCartBackend.shoppingCartBackend.repository.OrderRepository;
import com.shoppingCartBackend.shoppingCartBackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderSeeder implements CommandLineRunner {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) {

        // لا تعيد الإدخال إذا موجودة بيانات
        if (orderRepository.count() > 0) {
            return;
        }

        List<User> users = (List<User>) userRepository.findAll();

        if (users.isEmpty()) {
            System.out.println("❌ No users found");
            return;
        }

        for (int i = 1; i <= 100; i++) {

            Order order = new Order();

            order.setUser(users.get(i % users.size()));

            order.setOrderDate(LocalDateTime.now());

            order.setStatus(OrderStatus.PENDING);

            order.setTotalPrice(BigDecimal.valueOf(100 + i));

            orderRepository.save(order);
        }

        System.out.println("✅ 100 Orders Created Successfully");
    }
}