package com.market.ecommerce.dto;

import java.util.List;

public record OrderResponse(
    Long id,
    String totalAmount,
    String status,
    Long shippingAddressId,
    List<OrderItemResponse> items
) { }
