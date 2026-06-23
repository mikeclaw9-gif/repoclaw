package com.laundrypos.modules.sales.dto;

public record ServiceTypeResponse(
    Long id,
    String name,
    double pricePerKg,
    int estimatedTimeMinutes
) {}
