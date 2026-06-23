package com.laundrypos.modules.expenses.service;

import com.laundrypos.modules.expenses.dto.ExpenseRequest;
import com.laundrypos.modules.expenses.dto.ExpenseResponse;
import com.laundrypos.modules.expenses.model.Expense;
import com.laundrypos.modules.expenses.model.Expense.ExpenseCategory;
import com.laundrypos.modules.expenses.repository.ExpenseRepository;
import com.laundrypos.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public List<ExpenseResponse> findAll() {
        return expenseRepository.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    public ExpenseResponse findById(Long id) {
        var expense = expenseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Gasto", id));
        return toResponse(expense);
    }

    @Transactional
    public ExpenseResponse create(ExpenseRequest request) {
        var expense = Expense.builder()
            .description(request.description())
            .amount(request.amount())
            .category(request.category())
            .paymentMethod(request.paymentMethod())
            .expenseDate(request.expenseDate())
            .notes(request.notes())
            .build();
        return toResponse(expenseRepository.save(expense));
    }

    @Transactional
    public ExpenseResponse update(Long id, ExpenseRequest request) {
        var expense = expenseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Gasto", id));
        expense.setDescription(request.description());
        expense.setAmount(request.amount());
        expense.setCategory(request.category());
        expense.setPaymentMethod(request.paymentMethod());
        expense.setExpenseDate(request.expenseDate());
        expense.setNotes(request.notes());
        return toResponse(expenseRepository.save(expense));
    }

    @Transactional
    public void delete(Long id) {
        if (!expenseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Gasto", id);
        }
        expenseRepository.deleteById(id);
    }

    public List<ExpenseResponse> findByCategory(ExpenseCategory category) {
        return expenseRepository.findByCategory(category).stream()
            .map(this::toResponse)
            .toList();
    }

    public List<ExpenseResponse> findByDateRange(LocalDateTime start, LocalDateTime end) {
        return expenseRepository.findByExpenseDateBetween(start, end).stream()
            .map(this::toResponse)
            .toList();
    }

    public List<ExpenseResponse> search(String description) {
        return expenseRepository.findByDescriptionContainingIgnoreCase(description).stream()
            .map(this::toResponse)
            .toList();
    }

    private ExpenseResponse toResponse(Expense expense) {
        return new ExpenseResponse(
            expense.getId(),
            expense.getDescription(),
            expense.getAmount(),
            expense.getCategory(),
            expense.getPaymentMethod(),
            expense.getExpenseDate(),
            expense.getNotes(),
            expense.getCreatedAt());
    }
}
