package com.laundrypos.modules.inventory.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record StockMovementRequest(
    @NotNull Long productId,
    @Positive int quantity,
    String reason
) {}
