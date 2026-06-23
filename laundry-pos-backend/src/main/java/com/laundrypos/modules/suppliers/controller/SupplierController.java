package com.laundrypos.modules.suppliers.controller;

import com.laundrypos.modules.inventory.model.Product;
import com.laundrypos.modules.suppliers.dto.SupplierRequest;
import com.laundrypos.modules.suppliers.dto.SupplierResponse;
import com.laundrypos.modules.suppliers.service.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/suppliers")
@RequiredArgsConstructor
@Tag(name = "Proveedores", description = "Gestión de proveedores")
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping
    @Operation(summary = "Listar proveedores")
    public ResponseEntity<List<SupplierResponse>> findAll() {
        return ResponseEntity.ok(supplierService.findAll());
    }

    @PostMapping
    @Operation(summary = "Crear proveedor")
    public ResponseEntity<SupplierResponse> create(@Valid @RequestBody SupplierRequest request) {
        return ResponseEntity.ok(supplierService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar proveedor")
    public ResponseEntity<SupplierResponse> update(@PathVariable Long id,
                                                    @Valid @RequestBody SupplierRequest request) {
        return ResponseEntity.ok(supplierService.update(id, request));
    }

    @GetMapping("/{id}/purchase-history")
    @Operation(summary = "Historial de compras del proveedor")
    public ResponseEntity<List<Product>> getPurchaseHistory(@PathVariable Long id) {
        return ResponseEntity.ok(supplierService.getPurchaseHistory(id));
    }
}
