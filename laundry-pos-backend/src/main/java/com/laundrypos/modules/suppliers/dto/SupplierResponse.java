package com.laundrypos.modules.suppliers.dto;

public record SupplierResponse(
    Long id,
    String name,
    String contactName,
    String phone,
    String email,
    String address
) {}
