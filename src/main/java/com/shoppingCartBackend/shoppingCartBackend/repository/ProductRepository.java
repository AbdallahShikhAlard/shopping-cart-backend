package com.shoppingCartBackend.shoppingCartBackend.repository;

import com.shoppingCartBackend.shoppingCartBackend.model.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryName(String category);
    List<Product> findByBrand(String brand);
    List<Product> findByCategoryNameAndBrand(String category, String brand);
    List<Product> findByName(String name);
    List<Product> findByBrandAndName(String brand, String name);
    Long countByBrandAndName(String brand, String name);

    // Locks the product row until transaction completes
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdWithLock(@Param("id") Long id);

    // Atomic decrement — only succeeds if enough stock exists
    @Modifying
    @Query("UPDATE Product p SET p.inventory = p.inventory - :quantity " +
            "WHERE p.id = :id AND p.inventory >= :quantity")
    int decreaseInventory(@Param("id") Long id, @Param("quantity") int quantity);
}