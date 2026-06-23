package com.laundrypos.modules.expenses.repository;

import com.laundrypos.modules.expenses.model.Expense;
import com.laundrypos.modules.expenses.model.Expense.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByCategory(ExpenseCategory category);
    List<Expense> findByExpenseDateBetween(LocalDateTime start, LocalDateTime end);
    List<Expense> findByDescriptionContainingIgnoreCase(String description);
}
