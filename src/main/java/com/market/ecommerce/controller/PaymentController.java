package com.market.ecommerce.controller;

import com.market.ecommerce.dto.PaymentRequest;
import com.market.ecommerce.dto.PaymentResponse;
import com.market.ecommerce.entity.Order;
import com.market.ecommerce.entity.Payment;
import com.market.ecommerce.service.OrderService;
import com.market.ecommerce.service.PaymentService;
import com.market.ecommerce.security.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatusCode;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderService orderService;

    public PaymentController(PaymentService paymentService, OrderService orderService) {
        this.paymentService = paymentService;
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> makePayment(@Valid @RequestBody PaymentRequest request) {
        // Ensure order exists and fetch its user eagerly
        Order order = orderService.getOrderByIdWithUser(request.orderId());

        String currentEmail = SecurityUtils.getCurrentUserEmail();
        if (currentEmail == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User must be authenticated");
        }

        boolean isOwner = order.getUser() != null && currentEmail.equals(order.getUser().getEmail());
        boolean isAdmin = SecurityUtils.currentUserIsAdmin();

        if (!isOwner && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to pay this order");
        }

        Payment payment = paymentService.processPayment(request, order);

        PaymentResponse res = new PaymentResponse(payment.getId(), order.getId(), payment.getStatus().name(), payment.getAmount().toString());
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }
}
