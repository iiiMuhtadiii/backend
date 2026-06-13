package com.market.ecommerce.service;

import com.market.ecommerce.dto.PaymentRequest;
import com.market.ecommerce.entity.Order;
import com.market.ecommerce.entity.Payment;
import com.market.ecommerce.entity.PaymentMethod;
import com.market.ecommerce.entity.PaymentStatus;
import com.market.ecommerce.exception.BadRequestException;
import com.market.ecommerce.repository.OrderRepository;
import com.market.ecommerce.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Payment processPayment(PaymentRequest request, Order order) {
        if (order == null) throw new BadRequestException("Order not found");

        BigDecimal amount;
        try {
            amount = new BigDecimal(request.amount());
        } catch (NumberFormatException ex) {
            throw new BadRequestException("Invalid payment amount");
        }

        if (order.getTotalAmount() == null || order.getTotalAmount().compareTo(amount) != 0) {
            throw new BadRequestException("Payment amount does not match order total");
        }

        PaymentMethod method;
        try {
            method = PaymentMethod.valueOf(request.method());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid payment method");
        }

        Payment payment = Payment.builder()
                .order(order)
                .amount(amount)
                .method(method)
                .status(PaymentStatus.COMPLETED)
                .transactionId("SIMULATED_" + Instant.now().toEpochMilli())
                .build();

        Payment saved = paymentRepository.save(payment);

        // Optionally mark order as completed
        order.setStatus(com.market.ecommerce.entity.OrderStatus.COMPLETED);
        orderRepository.save(order);

        return saved;
    }
}
