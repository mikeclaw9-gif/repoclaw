package com.laundrypos.modules.sales.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ServiceOrderRequest(
    Long clientId,
    @NotNull Long userId,
    @NotNull List<OrderItemRequest> items,
    List<OrderProductRequest> products
) {
    public record OrderItemRequest(
        @NotNull Long serviceTypeId,
        @NotNull double weight
    ) {}

    public record OrderProductRequest(
        @NotNull Long productId,
        @NotNull int quantity
    ) {}
}
