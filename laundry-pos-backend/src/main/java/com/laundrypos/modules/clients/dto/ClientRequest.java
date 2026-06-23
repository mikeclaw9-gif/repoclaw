package com.laundrypos.modules.clients.dto;

import jakarta.validation.constraints.NotBlank;

public record ClientRequest(
    @NotBlank String name,
    @NotBlank String phone,
    String email
) {}
