package com.laundrypos.modules.reports.service;

import com.laundrypos.modules.clients.repository.ClientRepository;
import com.laundrypos.modules.inventory.repository.ProductRepository;
import com.laundrypos.modules.reports.dto.SalesReportResponse;
import com.laundrypos.modules.reports.dto.TopClientResponse;
import com.laundrypos.modules.reports.dto.TopProductResponse;
import com.laundrypos.modules.sales.repository.ServiceOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ServiceOrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ClientRepository clientRepository;

    public SalesReportResponse getDailySales(LocalDate date) {
        var start = date.atStartOfDay();
        var end = date.atTime(LocalTime.MAX);
        return buildSalesReport(start, end);
    }

    public SalesReportResponse getWeeklySales(LocalDate start, LocalDate end) {
        return buildSalesReport(start.atStartOfDay(), end.atTime(LocalTime.MAX));
    }

    public SalesReportResponse getMonthlySales(int month, int year) {
        var start = LocalDate.of(year, month, 1);
        var end = start.withDayOfMonth(start.lengthOfMonth());
        return buildSalesReport(start.atStartOfDay(), end.atTime(LocalTime.MAX));
    }

    public SalesReportResponse getYearlySales(int year) {
        var start = LocalDate.of(year, 1, 1);
        var end = LocalDate.of(year, 12, 31);
        return buildSalesReport(start.atStartOfDay(), end.atTime(LocalTime.MAX));
    }

    public List<TopProductResponse> getTopProducts() {
        return List.of();
    }

    public List<TopClientResponse> getTopClients() {
        return List.of();
    }

    private SalesReportResponse buildSalesReport(LocalDateTime start, LocalDateTime end) {
        var orders = orderRepository.findByCreatedAtBetween(start, end);
        double totalSales = orders.stream().mapToDouble(o -> o.getTotalAmount()).sum();

        return new SalesReportResponse(
            start.toLocalDate(), end.toLocalDate(), totalSales, orders.size(),
            orders.stream().map(o -> new SalesReportResponse.OrderSummary(
                o.getId(), o.getCreatedAt().toLocalDate(), o.getTotalAmount(), o.getStatus().name()
            )).toList());
    }
}
