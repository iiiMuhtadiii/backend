package com.market.ecommerce.controller;

import com.market.ecommerce.dto.AddToCartRequest;
import com.market.ecommerce.dto.CartItemResponse;
import com.market.ecommerce.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // إضافة للسلة باستخدام مسار الجذر مباشرة
    @PostMapping
    public ResponseEntity<CartItemResponse> addToCart(@Valid @RequestBody AddToCartRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.toDto(cartService.addToCart(request)));
    }

    @GetMapping
    public ResponseEntity<List<CartItemResponse>> getUserCart() {
        return ResponseEntity.ok(cartService.getUserCartDto());
    }

    @PutMapping("/{cartItemId}/quantity")
    public ResponseEntity<CartItemResponse> updateQuantity(@PathVariable Long cartItemId, @RequestParam Integer quantity) {
        return ResponseEntity.ok(cartService.toDto(cartService.updateQuantity(cartItemId, quantity)));
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> removeItem(@PathVariable Long cartItemId) {
        cartService.removeItem(cartItemId);
        return ResponseEntity.noContent().build();
    }
}
