package com.laundrypos.modules.cash_closure.controller;

import com.laundrypos.modules.cash_closure.dto.CashClosureRequest;
import com.laundrypos.modules.cash_closure.dto.CashClosureResponse;
import com.laundrypos.modules.cash_closure.dto.CashClosureSummaryResponse;
import com.laundrypos.modules.cash_closure.dto.CloseClosureRequest;
import com.laundrypos.modules.cash_closure.service.CashClosureService;
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
@RequestMapping("/cash-closure")
@RequiredArgsConstructor
@Tag(name = "Corte de Caja", description = "Gestión de cortes de caja")
public class CashClosureController {

    private final CashClosureService cashClosureService;

    @GetMapping
    @Operation(summary = "Listar todos los cortes de caja")
    public ResponseEntity<List<CashClosureResponse>> findAll() {
        return ResponseEntity.ok(cashClosureService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener corte de caja por ID")
    public ResponseEntity<CashClosureResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(cashClosureService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Abrir corte de caja")
    public ResponseEntity<CashClosureResponse> create(@Valid @RequestBody CashClosureRequest request) {
        return ResponseEntity.ok(cashClosureService.create(request));
    }

    @PostMapping("/{id}/close")
    @Operation(summary = "Cerrar corte de caja")
    public ResponseEntity<CashClosureResponse> close(
            @PathVariable Long id,
            @Valid @RequestBody CloseClosureRequest request) {
        return ResponseEntity.ok(cashClosureService.close(id, request));
    }

    @GetMapping("/summary")
    @Operation(summary = "Generar resumen de ventas y gastos por rango de fechas")
    public ResponseEntity<CashClosureSummaryResponse> generateSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(cashClosureService.generateSummary(start, end));
    }
}
