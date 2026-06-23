package com.laundrypos.modules.inventory.dto;

public record ProductResponse(
    Long id,
    String name,
    String category,
    double price,
    int stock,
    int minStock,
    Long supplierId,
    String supplierName,
    boolean lowStock
) {}
