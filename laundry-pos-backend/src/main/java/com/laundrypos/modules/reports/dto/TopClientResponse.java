package com.laundrypos.modules.reports.dto;

public record TopClientResponse(
    Long clientId,
    String clientName,
    long totalOrders,
    double totalSpent
) {}
