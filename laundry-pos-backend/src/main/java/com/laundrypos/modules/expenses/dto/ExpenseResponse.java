package com.laundrypos.modules.expenses.dto;

import com.laundrypos.modules.expenses.model.Expense.ExpenseCategory;
import com.laundrypos.modules.expenses.model.Expense.PaymentMethod;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ExpenseResponse(
    Long id,
    String description,
    BigDecimal amount,
    ExpenseCategory category,
    PaymentMethod paymentMethod,
    LocalDateTime expenseDate,
    String notes,
    LocalDateTime createdAt
) {}
