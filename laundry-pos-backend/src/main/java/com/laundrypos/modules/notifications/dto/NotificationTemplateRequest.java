package com.laundrypos.modules.notifications.dto;

import jakarta.validation.constraints.NotBlank;

public record NotificationTemplateRequest(
    @NotBlank String name,
    @NotBlank String content
) {}
