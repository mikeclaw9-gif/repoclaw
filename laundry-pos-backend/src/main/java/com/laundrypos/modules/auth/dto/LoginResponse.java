package com.laundrypos.modules.auth.dto;

public record LoginResponse(
    String token,
    String refreshToken,
    String username,
    String role
) {}
