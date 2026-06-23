package com.laundrypos.modules.cash_closure.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CloseClosureRequest(
    @NotBlank String closedBy,
    @NotNull BigDecimal declaredCash,
    String notes
) {}
