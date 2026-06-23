package com.laundrypos.modules.services.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ServiceResponse(
    Long id,
    String name,
    BigDecimal cost,
    boolean active,
    LocalDateTime createdAt
) {}
