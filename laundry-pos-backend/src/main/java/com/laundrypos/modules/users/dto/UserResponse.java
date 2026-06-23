package com.laundrypos.modules.users.dto;

public record UserResponse(
    Long id,
    String username,
    String role,
    boolean active
) {}
