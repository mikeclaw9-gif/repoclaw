package com.laundrypos.modules.sales.dto;

import java.time.LocalDateTime;
import java.util.List;

public record TicketResponse(
    Long id,
    String clientName,
    String userName,
    double totalWeight,
    double totalAmount,
    String status,
    LocalDateTime createdAt,
    List<TicketItemResponse> items,
    List<TicketProductResponse> products,
    String barcodeBase64
) {
    public record TicketItemResponse(
        String serviceTypeName,
        double weight,
        double price
    ) {}

    public record TicketProductResponse(
        String productName,
        int quantity,
        double unitPrice,
        double subtotal
    ) {}
}
