package com.market.ecommerce.service;

import com.market.ecommerce.dto.CheckoutRequest;
import com.market.ecommerce.dto.OrderItemResponse;
import com.market.ecommerce.dto.OrderResponse;
import com.market.ecommerce.entity.*;
import com.market.ecommerce.exception.BadRequestException;
import com.market.ecommerce.exception.ResourceNotFoundException;
import com.market.ecommerce.repository.*;
import com.market.ecommerce.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    public OrderService(OrderRepository orderRepository,
                        CartItemRepository cartRepository,
                        ProductRepository productRepository,
                        UserRepository userRepository,
                        AddressRepository addressRepository) {

        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
    }

    @Transactional
    public Order checkout(CheckoutRequest request) {
        String email = SecurityUtils.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("المستخدم غير موجود"));

        Address shippingAddress = addressRepository.findById(request.shippingAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("عنوان الشحن غير موجود"));

        if (!shippingAddress.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("عنوان الشحن لا يخص المستخدم الحالي");
        }

        List<CartItem> cartItems = cartRepository.findByUser(user);
        if (cartItems.isEmpty()) {
            throw new BadRequestException("سلة المشتريات فارغة");
        }

        Order order = Order.builder()
                .user(user)
                .shippingAddress(shippingAddress)
                .status(OrderStatus.NEW)
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem item : cartItems) {
            Long productId = item.getProduct().getId();
            int qty = item.getQuantity();

            int updated = productRepository.decrementStockIfAvailable(productId, qty);
            if (updated == 0) {
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new ResourceNotFoundException("المنتج غير موجود"));
                throw new BadRequestException("المخزون غير كافٍ للمنتج: " + product.getName());
            }

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("المنتج غير موجود"));

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(qty)
                    .price(product.getPrice())
                    .build();

            orderItems.add(orderItem);
            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(qty)));
        }

        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);
        cartRepository.deleteByUserId(user.getId());

        return savedOrder;
    }

    @Transactional
    public Order complete(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("الطلب غير موجود"));

        if (order.getStatus() == OrderStatus.COMPLETED) {
            throw new BadRequestException("الطلب مكتمل بالفعل");
        }

        order.setStatus(OrderStatus.COMPLETED);
        return orderRepository.save(order);
    }

    @Transactional
    public void cancel(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("الطلب غير موجود"));

        if (order.getStatus() == OrderStatus.COMPLETED) {
            throw new BadRequestException("لا يمكن إلغاء طلب مكتمل وشُحن بالفعل");
        }
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException("الطلب ملغي بالفعل");
        }

        order.setStatus(OrderStatus.CANCELLED);

        for (OrderItem item : order.getItems()) {
            Long productId = item.getProduct().getId();
            int qty = item.getQuantity();
            productRepository.incrementStock(productId, qty);
        }

        orderRepository.save(order);
    }

    // New: return the Order entity by id
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("الطلب غير موجود"));
    }

    // New: return the Order entity with user fetched
    public Order getOrderByIdWithUser(Long id) {
        return orderRepository.findByIdWithUser(id)
                .orElseThrow(() -> new ResourceNotFoundException("الطلب غير موجود"));
    }

    // New: return safe DTO for order detail
    public OrderResponse getOrderByIdDto(Long id) {
        Order order = getOrderById(id);

        var items = order.getItems().stream()
                .map(it -> new OrderItemResponse(
                        it.getId(),
                        it.getProduct().getId(),
                        it.getProduct().getName(),
                        it.getQuantity(),
                        it.getPrice().toString()
                ))
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getId(),
                order.getTotalAmount() != null ? order.getTotalAmount().toString() : "0.00",
                order.getStatus() != null ? order.getStatus().name() : "",
                order.getShippingAddress() != null ? order.getShippingAddress().getId() : null,
                items
        );
    }

    // Map single order to DTO
    public OrderResponse toDto(Order order) {
        var items = order.getItems().stream()
                .map(it -> new OrderItemResponse(
                        it.getId(),
                        it.getProduct().getId(),
                        it.getProduct().getName(),
                        it.getQuantity(),
                        it.getPrice().toString()
                ))
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getId(),
                order.getTotalAmount() != null ? order.getTotalAmount().toString() : "0.00",
                order.getStatus() != null ? order.getStatus().name() : "",
                order.getShippingAddress() != null ? order.getShippingAddress().getId() : null,
                items
        );
    }

    // New: return OrderResponse DTO after checkout
    public OrderResponse checkoutDto(CheckoutRequest request) {
        Order order = checkout(request);
        return toDto(order);
    }

    // New: return list of DTOs for current user's orders
    public List<OrderResponse> getUserOrdersDto() {
        String email = SecurityUtils.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("المستخدم غير موجود"));

        List<Order> orders = orderRepository.findByUserId(user.getId());
        return orders.stream().map(this::toDto).collect(Collectors.toList());
    }

    // New: return list of DTOs for all orders (admin)
    public List<OrderResponse> getAllOrdersDto() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(this::toDto).collect(Collectors.toList());
    }
}
