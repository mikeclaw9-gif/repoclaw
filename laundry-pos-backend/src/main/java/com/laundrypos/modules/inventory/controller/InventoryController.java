package com.laundrypos.modules.inventory.controller;

import com.laundrypos.modules.inventory.dto.ProductRequest;
import com.laundrypos.modules.inventory.dto.ProductResponse;
import com.laundrypos.modules.inventory.dto.StockMovementRequest;
import com.laundrypos.modules.inventory.model.InventoryMovement;
import com.laundrypos.modules.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventario", description = "Gestión de productos e inventario")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/products")
    @Operation(summary = "Listar productos")
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(inventoryService.getAllProducts());
    }

    @PostMapping("/products")
    @Operation(summary = "Crear producto")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(inventoryService.createProduct(request));
    }

    @PutMapping("/products/{id}")
    @Operation(summary = "Actualizar producto")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id,
                                                          @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(inventoryService.updateProduct(id, request));
    }

    @GetMapping("/stock-movements")
    @Operation(summary = "Movimientos de stock")
    public ResponseEntity<List<InventoryMovement>> getStockMovements() {
        return ResponseEntity.ok(inventoryService.getStockMovements());
    }

    @PostMapping("/stock-entry")
    @Operation(summary = "Registrar entrada de stock")
    public ResponseEntity<InventoryMovement> stockEntry(@Valid @RequestBody StockMovementRequest request) {
        return ResponseEntity.ok(inventoryService.stockEntry(request));
    }

    @PostMapping("/stock-exit")
    @Operation(summary = "Registrar salida de stock")
    public ResponseEntity<InventoryMovement> stockExit(@Valid @RequestBody StockMovementRequest request) {
        return ResponseEntity.ok(inventoryService.stockExit(request));
    }

    @GetMapping("/low-stock-alerts")
    @Operation(summary = "Alertas de stock bajo")
    public ResponseEntity<List<ProductResponse>> getLowStockAlerts() {
        return ResponseEntity.ok(inventoryService.getLowStockAlerts());
    }
}
