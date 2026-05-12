package com.shoppingCartBackend.shoppingCartBackend.repository;

import com.shoppingCartBackend.shoppingCartBackend.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);

    List<Order> findByOrderDateBetween(
            LocalDateTime start,
            LocalDateTime end
    );
}
