package com.laundrypos.modules.services.controller;

import com.laundrypos.modules.services.dto.ServiceRequest;
import com.laundrypos.modules.services.dto.ServiceResponse;
import com.laundrypos.modules.services.service.ServiceEntityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
@Tag(name = "Servicios", description = "Gestión de servicios de lavandería")
public class ServiceController {

    private final ServiceEntityService service;

    @GetMapping
    @Operation(summary = "Listar todos los servicios")
    public ResponseEntity<List<ServiceResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/active")
    @Operation(summary = "Listar servicios activos")
    public ResponseEntity<List<ServiceResponse>> findActive() {
        return ResponseEntity.ok(service.findActive());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener servicio por ID")
    public ResponseEntity<ServiceResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    @Operation(summary = "Crear servicio")
    public ResponseEntity<ServiceResponse> create(@Valid @RequestBody ServiceRequest request) {
        var result = service.create(request);
        return ResponseEntity.created(URI.create("/services/" + result.id())).body(result);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar servicio")
    public ResponseEntity<ServiceResponse> update(@PathVariable Long id, @Valid @RequestBody ServiceRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar servicio")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-status")
    @Operation(summary = "Activar/desactivar servicio")
    public ResponseEntity<ServiceResponse> toggleStatus(@PathVariable Long id) {
        return ResponseEntity.ok(service.toggleStatus(id));
    }
}
