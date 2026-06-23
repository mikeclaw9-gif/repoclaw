package com.laundrypos.modules.reports.dto;

import java.time.LocalDate;
import java.util.List;

public record SalesReportResponse(
    LocalDate startDate,
    LocalDate endDate,
    double totalSales,
    long totalOrders,
    List<OrderSummary> orders
) {
    public record OrderSummary(
        Long id,
        LocalDate date,
        double amount,
        String status
    ) {}
}
