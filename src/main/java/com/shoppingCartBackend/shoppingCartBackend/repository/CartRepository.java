package com.shoppingCartBackend.shoppingCartBackend.repository;

import com.shoppingCartBackend.shoppingCartBackend.model.Cart;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Cart findByUserId(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT c
        FROM Cart c
        WHERE c.user.id = :userId
    """)
    Cart findByUserIdForUpdate(Long userId);
}