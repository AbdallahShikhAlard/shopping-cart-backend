package com.shoppingCartBackend.shoppingCartBackend.service.order;

import com.shoppingCartBackend.shoppingCartBackend.dto.OrderDto;
import com.shoppingCartBackend.shoppingCartBackend.enums.OrderStatus;
import com.shoppingCartBackend.shoppingCartBackend.exeptions.ResourceNotFoundException;
import com.shoppingCartBackend.shoppingCartBackend.model.Cart;
import com.shoppingCartBackend.shoppingCartBackend.model.Order;
import com.shoppingCartBackend.shoppingCartBackend.model.OrderItem;
import com.shoppingCartBackend.shoppingCartBackend.model.Product;
import com.shoppingCartBackend.shoppingCartBackend.repository.CartRepository;
import com.shoppingCartBackend.shoppingCartBackend.repository.OrderRepository;
import com.shoppingCartBackend.shoppingCartBackend.repository.ProductRepository;
import com.shoppingCartBackend.shoppingCartBackend.service.cart.CartService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;
    private final ModelMapper modelMapper;
    private final CartRepository cartRepository;

    @Override
    @Transactional  // entire order is one atomic transaction
    public Order placeOrder(Long userId) {
        Cart cart = cartService.getCartByUserId(userId);
        Order order = createOrder(cart);
        List<OrderItem> orderItemList = createOrderItems(order, cart);

        order.setOrderItems(new HashSet<>(orderItemList));
        order.setTotalPrice(calculateTotalPrice(orderItemList));

        Order savedOrder = orderRepository.save(order);

        cart.getItems().clear();
        cart.setTotalPrice(BigDecimal.ZERO);
        cartRepository.save(cart);

        return savedOrder;
    }

    private Order createOrder(Cart cart) {
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    private List<OrderItem> createOrderItems(Order order, Cart cart) {
        return cart.getItems().stream().map(cartItem -> {
            Product product = cartItem.getProduct();
            int quantity = cartItem.getQuantity();

            // Atomically decrement inventory — only succeeds if stock >= quantity
            int updated = productRepository.decreaseInventory(product.getId(), quantity);

            if (updated == 0) {
                throw new ResourceNotFoundException(
                        "Sorry, insufficient stock for product: " + product.getName()
                );
            }

            return new OrderItem(
                    quantity,
                    cartItem.getUnitPrice(),
                    order,
                    product);
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
}