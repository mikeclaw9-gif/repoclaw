package com.laundrypos.modules.suppliers.dto;

import jakarta.validation.constraints.NotBlank;

public record SupplierRequest(
    @NotBlank String name,
    String contactName,
    @NotBlank String phone,
    String email,
    String address
) {}
