package com.shoppingCartBackend.shoppingCartBackend.repository;

import com.shoppingCartBackend.shoppingCartBackend.model.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository
        extends JpaRepository<Product, Long> {

    List<Product> findByCategoryName(String category);
    List<Product> findByBrand(String brand);
    List<Product> findByCategoryNameAndBrand(String category, String brand);
    List<Product> findByName(String name);
    List<Product> findByBrandAndName(String brand, String name);
    Long countByBrandAndName(String brand, String name);

    // =========================================
    // PESSIMISTIC LOCK
    // =========================================
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT p
        FROM Product p
        WHERE p.id = :id
    """)
    Optional<Product> findByIdForUpdate(@Param("id") Long id);

    // =========================================
    // EAGER FETCH IMAGES — for list endpoint
    // =========================================
    @Query("""
        SELECT DISTINCT p
        FROM Product p
        LEFT JOIN FETCH p.images
    """)
    List<Product> findAllWithImages();

    // =========================================
    // EAGER FETCH IMAGES — for single product endpoint
    // =========================================
    @Query("""
        SELECT p
        FROM Product p
        LEFT JOIN FETCH p.images
        WHERE p.id = :id
    """)
    Optional<Product> findByIdWithImages(@Param("id") Long id);
}