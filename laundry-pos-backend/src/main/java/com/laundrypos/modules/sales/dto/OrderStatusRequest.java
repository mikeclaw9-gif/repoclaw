package com.laundrypos.modules.sales.dto;

import jakarta.validation.constraints.NotBlank;

public record OrderStatusRequest(
    @NotBlank String status
) {}
