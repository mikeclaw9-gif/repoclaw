package com.laundrypos.modules.expenses.controller;

import com.laundrypos.modules.expenses.dto.ExpenseRequest;
import com.laundrypos.modules.expenses.dto.ExpenseResponse;
import com.laundrypos.modules.expenses.model.Expense.ExpenseCategory;
import com.laundrypos.modules.expenses.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/expenses")
@RequiredArgsConstructor
@Tag(name = "Gastos", description = "Gestión de gastos operativos")
public class ExpenseController {

    private final ExpenseService expenseService;

    @GetMapping
    @Operation(summary = "Listar todos los gastos")
    public ResponseEntity<List<ExpenseResponse>> findAll() {
        return ResponseEntity.ok(expenseService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener gasto por ID")
    public ResponseEntity<ExpenseResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(expenseService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Crear gasto")
    public ResponseEntity<ExpenseResponse> create(@Valid @RequestBody ExpenseRequest request) {
        return ResponseEntity.ok(expenseService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar gasto")
    public ResponseEntity<ExpenseResponse> update(@PathVariable Long id, @Valid @RequestBody ExpenseRequest request) {
        return ResponseEntity.ok(expenseService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar gasto")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        expenseService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Buscar gastos por categoría")
    public ResponseEntity<List<ExpenseResponse>> findByCategory(@PathVariable ExpenseCategory category) {
        return ResponseEntity.ok(expenseService.findByCategory(category));
    }

    @GetMapping("/date-range")
    @Operation(summary = "Buscar gastos por rango de fechas")
    public ResponseEntity<List<ExpenseResponse>> findByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(expenseService.findByDateRange(start, end));
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar gastos por descripción")
    public ResponseEntity<List<ExpenseResponse>> search(@RequestParam String description) {
        return ResponseEntity.ok(expenseService.search(description));
    }
}
