package com.laundrypos.modules.clients.dto;

import java.time.LocalDateTime;

public record ClientResponse(
    Long id,
    String name,
    String phone,
    String email,
    int loyaltyPoints,
    LocalDateTime createdAt
) {}
