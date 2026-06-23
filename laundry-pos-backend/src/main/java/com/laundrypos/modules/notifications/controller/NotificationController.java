package com.laundrypos.modules.notifications.controller;

import com.laundrypos.modules.notifications.dto.NotificationTemplateRequest;
import com.laundrypos.modules.notifications.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Tag(name = "Notificaciones", description = "Envío de notificaciones a clientes")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/send-ready/{orderId}")
    @Operation(summary = "Enviar notificación de orden lista")
    public ResponseEntity<Void> sendReadyNotification(@PathVariable Long orderId) {
        notificationService.sendReadyNotification(orderId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/templates")
    @Operation(summary = "Listar plantillas de notificación")
    public ResponseEntity<List<String>> getTemplates() {
        return ResponseEntity.ok(notificationService.getTemplates());
    }

    @PutMapping("/templates/{id}")
    @Operation(summary = "Actualizar plantilla")
    public ResponseEntity<Void> updateTemplate(@PathVariable Long id,
                                                @Valid @RequestBody NotificationTemplateRequest request) {
        notificationService.updateTemplate(id, request);
        return ResponseEntity.ok().build();
    }
}
