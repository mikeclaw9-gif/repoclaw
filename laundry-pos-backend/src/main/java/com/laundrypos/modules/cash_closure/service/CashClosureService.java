package com.laundrypos.modules.cash_closure.service;

import com.laundrypos.modules.cash_closure.dto.CashClosureRequest;
import com.laundrypos.modules.cash_closure.dto.CashClosureResponse;
import com.laundrypos.modules.cash_closure.dto.CashClosureSummaryResponse;
import com.laundrypos.modules.cash_closure.dto.CloseClosureRequest;
import com.laundrypos.modules.cash_closure.model.CashClosure;
import com.laundrypos.modules.cash_closure.model.CashClosure.CashClosureStatus;
import com.laundrypos.modules.cash_closure.repository.CashClosureRepository;
import com.laundrypos.modules.expenses.model.Expense;
import com.laundrypos.modules.expenses.model.Expense.ExpenseCategory;
import com.laundrypos.modules.expenses.repository.ExpenseRepository;
import com.laundrypos.modules.sales.model.ServiceOrder;
import com.laundrypos.modules.sales.model.ServiceOrder.OrderStatus;
import com.laundrypos.modules.sales.repository.ServiceOrderRepository;
import com.laundrypos.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CashClosureService {

    private final CashClosureRepository cashClosureRepository;
    private final ServiceOrderRepository serviceOrderRepository;
    private final ExpenseRepository expenseRepository;

    public List<CashClosureResponse> findAll() {
        return cashClosureRepository.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    public CashClosureResponse findById(Long id) {
        var closure = cashClosureRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Corte de caja", id));
        return toResponse(closure);
    }

    @Transactional
    public CashClosureResponse create(CashClosureRequest request) {
        var initialCash = request.initialCash() != null ? request.initialCash() : BigDecimal.ZERO;
        var sales = getTotalSales(request.openedAt(), request.closedAt());
        var expenses = getTotalExpenses(request.openedAt(), request.closedAt());

        var closure = CashClosure.builder()
            .openedAt(request.openedAt())
            .closedAt(request.closedAt())
            .openedBy(request.openedBy())
            .initialCash(initialCash)
            .totalSales(sales)
            .totalExpenses(expenses)
            .expectedCash(initialCash.add(sales).subtract(expenses))
            .status(CashClosureStatus.ABIERTO)
            .notes(request.notes())
            .build();
        return toResponse(cashClosureRepository.save(closure));
    }

    @Transactional
    public CashClosureResponse close(Long id, CloseClosureRequest request) {
        var closure = cashClosureRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Corte de caja", id));

        if (closure.getStatus() == CashClosureStatus.CERRADO) {
            throw new IllegalArgumentException("El corte de caja ya está cerrado");
        }

        closure.setClosedBy(request.closedBy());
        closure.setDeclaredCash(request.declaredCash());
        closure.setDifference(request.declaredCash().subtract(closure.getExpectedCash()));
        closure.setStatus(CashClosureStatus.CERRADO);

        if (request.notes() != null) {
            closure.setNotes(request.notes());
        }

        return toResponse(cashClosureRepository.save(closure));
    }

    public CashClosureSummaryResponse generateSummary(LocalDateTime start, LocalDateTime end) {
        var orders = serviceOrderRepository.findByCreatedAtBetween(start, end);
        var expenses = expenseRepository.findByExpenseDateBetween(start, end);

        var totalOrders = orders.size();
        var completedOrders = orders.stream()
            .filter(o -> o.getStatus() == OrderStatus.ENTREGADO)
            .count();
        var pendingOrders = orders.stream()
            .filter(o -> o.getStatus() != OrderStatus.ENTREGADO)
            .count();
        var totalSales = orders.stream()
            .filter(o -> o.getStatus() == OrderStatus.ENTREGADO)
            .map(ServiceOrder::getTotalAmount)
            .reduce(0.0, Double::sum);
        var totalExpenses = expenses.stream()
            .map(Expense::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        var expensesByCategory = expenses.stream()
            .collect(Collectors.groupingBy(
                Expense::getCategory,
                LinkedHashMap::new,
                Collectors.reducing(BigDecimal.ZERO, Expense::getAmount, BigDecimal::add)
            ));

        Stream.of(ExpenseCategory.values()).forEach(cat ->
            expensesByCategory.putIfAbsent(cat, BigDecimal.ZERO));

        return new CashClosureSummaryResponse(
            totalOrders,
            completedOrders,
            pendingOrders,
            BigDecimal.valueOf(totalSales),
            totalExpenses,
            BigDecimal.valueOf(totalSales).subtract(totalExpenses),
            expensesByCategory
        );
    }

    private BigDecimal getTotalSales(LocalDateTime start, LocalDateTime end) {
        var orders = serviceOrderRepository.findByCreatedAtBetween(start, end);
        return BigDecimal.valueOf(orders.stream()
            .filter(o -> o.getStatus() == OrderStatus.ENTREGADO)
            .map(ServiceOrder::getTotalAmount)
            .reduce(0.0, Double::sum));
    }

    private BigDecimal getTotalExpenses(LocalDateTime start, LocalDateTime end) {
        return expenseRepository.findByExpenseDateBetween(start, end).stream()
            .map(Expense::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private CashClosureResponse toResponse(CashClosure closure) {
        return new CashClosureResponse(
            closure.getId(),
            closure.getOpenedAt(),
            closure.getClosedAt(),
            closure.getOpenedBy(),
            closure.getClosedBy(),
            closure.getInitialCash(),
            closure.getTotalSales(),
            closure.getTotalExpenses(),
            closure.getExpectedCash(),
            closure.getDeclaredCash(),
            closure.getDifference(),
            closure.getStatus(),
            closure.getNotes(),
            closure.getCreatedAt());
    }
}
