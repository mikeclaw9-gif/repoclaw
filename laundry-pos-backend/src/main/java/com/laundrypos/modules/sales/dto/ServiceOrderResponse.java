package com.laundrypos.modules.sales.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ServiceOrderResponse(
    Long id,
    Long clientId,
    String clientName,
    Long userId,
    String userName,
    double totalWeight,
    double totalAmount,
    String status,
    LocalDateTime createdAt,
    LocalDateTime readyAt,
    List<OrderItemResponse> items,
    List<OrderProductResponse> products
) {
    public record OrderItemResponse(
        Long id,
        String serviceTypeName,
        double weight,
        double price
    ) {}

    public record OrderProductResponse(
        Long id,
        String productName,
        int quantity,
        double unitPrice,
        double subtotal
    ) {}
}
