package com.laundrypos.modules.reports.dto;

public record TopProductResponse(
    Long productId,
    String productName,
    long totalQuantity,
    double totalRevenue
) {}
