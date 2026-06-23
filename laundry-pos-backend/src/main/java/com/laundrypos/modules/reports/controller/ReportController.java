package com.laundrypos.modules.reports.controller;

import com.laundrypos.modules.reports.dto.SalesReportResponse;
import com.laundrypos.modules.reports.dto.TopClientResponse;
import com.laundrypos.modules.reports.dto.TopProductResponse;
import com.laundrypos.modules.reports.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@Tag(name = "Reportes", description = "Reportes y análisis de ventas")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/sales/daily")
    @Operation(summary = "Reporte de ventas diario")
    public ResponseEntity<SalesReportResponse> getDailySales(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(reportService.getDailySales(date));
    }

    @GetMapping("/sales/weekly")
    @Operation(summary = "Reporte de ventas semanal")
    public ResponseEntity<SalesReportResponse> getWeeklySales(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(reportService.getWeeklySales(start, end));
    }

    @GetMapping("/sales/monthly")
    @Operation(summary = "Reporte de ventas mensual")
    public ResponseEntity<SalesReportResponse> getMonthlySales(@RequestParam int month, @RequestParam int year) {
        return ResponseEntity.ok(reportService.getMonthlySales(month, year));
    }

    @GetMapping("/sales/yearly")
    @Operation(summary = "Reporte de ventas anual")
    public ResponseEntity<SalesReportResponse> getYearlySales(@RequestParam int year) {
        return ResponseEntity.ok(reportService.getYearlySales(year));
    }

    @GetMapping("/top-products")
    @Operation(summary = "Productos más vendidos")
    public ResponseEntity<List<TopProductResponse>> getTopProducts() {
        return ResponseEntity.ok(reportService.getTopProducts());
    }

    @GetMapping("/top-clients")
    @Operation(summary = "Clientes frecuentes")
    public ResponseEntity<List<TopClientResponse>> getTopClients() {
        return ResponseEntity.ok(reportService.getTopClients());
    }
}
