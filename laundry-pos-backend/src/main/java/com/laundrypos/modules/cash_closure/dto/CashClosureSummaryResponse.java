package com.laundrypos.modules.cash_closure.dto;

import com.laundrypos.modules.expenses.model.Expense.ExpenseCategory;
import java.math.BigDecimal;
import java.util.Map;

public record CashClosureSummaryResponse(
    long totalOrders,
    long completedOrders,
    long pendingOrders,
    BigDecimal totalSales,
    BigDecimal totalExpenses,
    BigDecimal netRevenue,
    Map<ExpenseCategory, BigDecimal> expensesByCategory
) {}
