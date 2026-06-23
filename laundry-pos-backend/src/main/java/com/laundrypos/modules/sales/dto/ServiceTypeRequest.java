package com.laundrypos.modules.sales.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ServiceTypeRequest(
    @NotBlank String name,
    @NotNull @Positive double pricePerKg,
    int estimatedTimeMinutes
) {}
