package com.laundrypos.modules.cash_closure.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CashClosureRequest(
    @NotNull LocalDateTime openedAt,
    @NotNull LocalDateTime closedAt,
    @NotBlank String openedBy,
    BigDecimal initialCash,
    String notes
) {}
