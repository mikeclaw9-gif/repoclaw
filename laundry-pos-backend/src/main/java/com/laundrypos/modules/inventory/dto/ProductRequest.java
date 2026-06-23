package com.laundrypos.modules.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record ProductRequest(
    @NotBlank String name,
    @NotBlank String category,
    @Positive double price,
    int stock,
    int minStock,
    Long supplierId
) {}
