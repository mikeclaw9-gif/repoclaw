package com.laundrypos.modules.cash_closure.dto;

import com.laundrypos.modules.cash_closure.model.CashClosure.CashClosureStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CashClosureResponse(
    Long id,
    LocalDateTime openedAt,
    LocalDateTime closedAt,
    String openedBy,
    String closedBy,
    BigDecimal initialCash,
    BigDecimal totalSales,
    BigDecimal totalExpenses,
    BigDecimal expectedCash,
    BigDecimal declaredCash,
    BigDecimal difference,
    CashClosureStatus status,
    String notes,
    LocalDateTime createdAt
) {}
