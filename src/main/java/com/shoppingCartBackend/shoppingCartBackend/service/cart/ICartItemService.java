package com.shoppingCartBackend.shoppingCartBackend.service.cart;

import com.shoppingCartBackend.shoppingCartBackend.model.CartItem;

public interface ICartItemService {
    void addItemToCart(Long cartId, Long itemId, int quantity);
    void removeItemFromCart(Long cartId, Long itemId);
    void updateItemQuantity(Long cartId, Long itemId, int quantity);

    CartItem getCartItem(Long cartId, Long productId);
}
