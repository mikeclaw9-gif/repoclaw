package com.laundrypos.modules.users.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(
    @NotBlank String username,
    @NotBlank @Size(min = 6) String password,
    @NotBlank String role
) {}
