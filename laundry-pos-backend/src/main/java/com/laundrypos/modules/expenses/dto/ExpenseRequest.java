package com.laundrypos.modules.expenses.dto;

import com.laundrypos.modules.expenses.model.Expense.ExpenseCategory;
import com.laundrypos.modules.expenses.model.Expense.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ExpenseRequest(
    @NotBlank String description,
    @NotNull @Positive BigDecimal amount,
    @NotNull ExpenseCategory category,
    @NotNull PaymentMethod paymentMethod,
    @NotNull LocalDateTime expenseDate,
    String notes
) {}
