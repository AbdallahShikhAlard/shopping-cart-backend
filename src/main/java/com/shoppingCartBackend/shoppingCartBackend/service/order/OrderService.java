package com.shoppingCartBackend.shoppingCartBackend.service.order;

import com.shoppingCartBackend.shoppingCartBackend.dto.BatchResultResponse;
import com.shoppingCartBackend.shoppingCartBackend.dto.OrderDto;
import com.shoppingCartBackend.shoppingCartBackend.enums.OrderStatus;
import com.shoppingCartBackend.shoppingCartBackend.exeptions.ResourceNotFoundException;
import com.shoppingCartBackend.shoppingCartBackend.model.*;
import com.shoppingCartBackend.shoppingCartBackend.repository.CartItemRepository;
import com.shoppingCartBackend.shoppingCartBackend.repository.CartRepository;
import com.shoppingCartBackend.shoppingCartBackend.repository.OrderRepository;
import com.shoppingCartBackend.shoppingCartBackend.repository.ProductRepository;
import com.shoppingCartBackend.shoppingCartBackend.service.cart.CartService;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor

public class OrderService implements IOrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;
    private final ModelMapper modelMapper;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;


    @Transactional
    @Override
    public Order placeOrder(Long userId) {
        try {
            // ── LOCK CART ─────────────────────────────────────────────
            Cart cart = cartRepository.findByUserIdForUpdate(userId);

            if (cart == null) {
                throw new RuntimeException("Cart not found");
            }
            if (cart.getItems().isEmpty()) {
                throw new RuntimeException("Cart is empty");
            }

            // ── CREATE ORDER ──────────────────────────────────────────
            Order order = new Order();
            order.setUser(cart.getUser());
            order.setStatus(OrderStatus.PENDING);
            order.setOrderDate(LocalDateTime.now());

            List<OrderItem> orderItems = new ArrayList<>();
            BigDecimal totalPrice = BigDecimal.ZERO;

            // ── PROCESS PRODUCTS ──────────────────────────────────────
            for (CartItem item : cart.getItems()) {

                Product product = productRepository
                        .findByIdForUpdate(item.getProduct().getId())
                        .orElseThrow(() ->
                                new RuntimeException("Product not found"));

                // Prevent overselling
                if (product.getInventory() < item.getQuantity()) {
                    throw new RuntimeException(
                            "Out of stock: " + product.getName()
                    );
                }

                // Update inventory
                product.setInventory(
                        product.getInventory() - item.getQuantity());
                productRepository.saveAndFlush(product);

                // Create order item
                OrderItem orderItem = new OrderItem(
                        item.getQuantity(),
                        item.getUnitPrice(),
                        order,
                        product);
                orderItems.add(orderItem);

                // Calculate total
                totalPrice = totalPrice.add(
                        item.getUnitPrice().multiply(
                                BigDecimal.valueOf(item.getQuantity())));


                if (true) {
                    throw new RuntimeException("SIMULATED CRASH FOR ACID TEST");
                }
            }

            // ── FINALIZE ORDER ────────────────────────────────────────
            order.setOrderItems(new HashSet<>(orderItems));
            order.setTotalPrice(totalPrice);
            Order savedOrder = orderRepository.saveAndFlush(order);

            // ── CLEAR CART ────────────────────────────────────────────
            cart.getItems().clear();
            cart.setTotalPrice(BigDecimal.ZERO);
            cartRepository.save(cart);

            return savedOrder;

        } catch (ObjectOptimisticLockingFailureException e) {
            // Optimistic lock triggered — version mismatch
            throw new RuntimeException(
                    "High demand on this product. Please try again.");

        } catch (PessimisticLockingFailureException e) {
            // Pessimistic lock triggered — deadlock or timeout
            throw new RuntimeException(
                    "System is busy processing orders. Please try again.");
        }
    }
    private Order createOrder(Cart cart) {
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    private List<OrderItem> createOrderItems(
            Order order,
            Cart cart
    ) {

        return cart.getItems().stream().map(cartItem -> {

            Product product = productRepository
                    .findByIdForUpdate(
                            cartItem.getProduct().getId()
                    )
                    .orElseThrow(
                            () -> new RuntimeException(
                                    "Product not found"
                            )
                    );

            if (product.getInventory() < cartItem.getQuantity()) {
                throw new RuntimeException("Out of stock");
            }

            product.setInventory(
                    product.getInventory()
                            - cartItem.getQuantity()
            );

            // التعديل هنا
            productRepository.saveAndFlush(product);

            return new OrderItem(
                    cartItem.getQuantity(),
                    cartItem.getUnitPrice(),
                    order,
                    product
            );

        }).toList();
    }

    private BigDecimal calculateTotalPrice(List<OrderItem> orderItemList) {
        return orderItemList
                .stream()
                .map(item -> item.getPrice()
                        .multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public OrderDto getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    @Override
    public List<OrderDto> getUserOrders(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream().map(this::convertToDto).toList();
    }

    @Override
    public OrderDto convertToDto(Order order) {
        return modelMapper.map(order, OrderDto.class);
    }

    // =========================================
// BEFORE
// =========================================
    public BatchResultResponse processDailySalesWithoutChunking() {

        long startTime = System.currentTimeMillis();

        List<Order> todayOrders = orderRepository.findAll();

        BigDecimal totalSales = BigDecimal.ZERO;

        for (Order order : todayOrders) {

            try {

                // Simulate heavy database/report processing
                Thread.sleep(200);

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
            }

            totalSales =
                    totalSales.add(order.getTotalPrice());
        }

        long endTime = System.currentTimeMillis();

        return new BatchResultResponse(
                todayOrders.size(),
                totalSales,
                (endTime - startTime),
                "WITHOUT_CHUNKING"
        );
    }


// =========================================
// AFTER
// =========================================

    public BatchResultResponse processDailySalesWithChunking() {

        long startTime = System.currentTimeMillis();

        List<Order> todayOrders = orderRepository.findAll();

        int chunkSize = 5;

        List<CompletableFuture<BigDecimal>> futures =
                new ArrayList<>();

        for (int i = 0; i < todayOrders.size(); i += chunkSize) {

            List<Order> chunk = todayOrders.subList(
                    i,
                    Math.min(i + chunkSize, todayOrders.size())            );

            CompletableFuture<BigDecimal> future =
                    CompletableFuture.supplyAsync(
                            () -> processSalesChunk(chunk)
                    );

            futures.add(future);
        }

        BigDecimal totalSales = futures.stream()
                .map(CompletableFuture::join)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long endTime = System.currentTimeMillis();

        return new BatchResultResponse(
                todayOrders.size(),
                totalSales,
                (endTime - startTime),
                "WITH_CHUNKING"
        );
    }


// =========================================
// PROCESS SALES CHUNK
// =========================================

    private BigDecimal processSalesChunk(List<Order> chunk) {

        BigDecimal chunkTotal = BigDecimal.ZERO;

        for (Order order : chunk) {

            try {

                // Simulate heavy database/report processing
                Thread.sleep(200);

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
            }

            chunkTotal =
                    chunkTotal.add(order.getTotalPrice());
        }

        return chunkTotal;
    }
}
