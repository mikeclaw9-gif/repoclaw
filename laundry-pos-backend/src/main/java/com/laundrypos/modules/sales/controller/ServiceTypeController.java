package com.laundrypos.modules.sales.controller;

import com.laundrypos.modules.sales.dto.ServiceTypeRequest;
import com.laundrypos.modules.sales.dto.ServiceTypeResponse;
import com.laundrypos.modules.sales.service.ServiceTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/sales/service-types")
@RequiredArgsConstructor
@Tag(name = "Tipos de Servicio (Ventas)", description = "Gestión de tipos de servicio para órdenes de venta")
public class ServiceTypeController {

    private final ServiceTypeService serviceTypeService;

    @GetMapping
    @Operation(summary = "Listar todos los tipos de servicio")
    public ResponseEntity<List<ServiceTypeResponse>> findAll() {
        return ResponseEntity.ok(serviceTypeService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener tipo de servicio por ID")
    public ResponseEntity<ServiceTypeResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(serviceTypeService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Crear tipo de servicio")
    public ResponseEntity<ServiceTypeResponse> create(@Valid @RequestBody ServiceTypeRequest request) {
        var result = serviceTypeService.create(request);
        return ResponseEntity.created(URI.create("/sales/service-types/" + result.id())).body(result);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar tipo de servicio")
    public ResponseEntity<ServiceTypeResponse> update(@PathVariable Long id, @Valid @RequestBody ServiceTypeRequest request) {
        return ResponseEntity.ok(serviceTypeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar tipo de servicio")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        serviceTypeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
