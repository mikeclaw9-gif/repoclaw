package com.laundrypos.modules.sales.controller;

import com.laundrypos.modules.sales.dto.OrderStatusRequest;
import com.laundrypos.modules.sales.dto.ServiceOrderRequest;
import com.laundrypos.modules.sales.dto.ServiceOrderResponse;
import com.laundrypos.modules.sales.dto.TicketResponse;
import com.laundrypos.modules.sales.service.SalesService;
import com.laundrypos.modules.sales.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sales")
@RequiredArgsConstructor
@Tag(name = "Ventas/POS", description = "Gestión de ventas y órdenes de servicio")
public class SalesController {

    private final SalesService salesService;
    private final TicketService ticketService;

    @PostMapping("/service-order")
    @Operation(summary = "Crear orden de servicio")
    public ResponseEntity<ServiceOrderResponse> createOrder(@Valid @RequestBody ServiceOrderRequest request) {
        return ResponseEntity.ok(salesService.createOrder(request));
    }

    @GetMapping("/orders")
    @Operation(summary = "Listar todas las órdenes")
    public ResponseEntity<List<ServiceOrderResponse>> getAllOrders() {
        return ResponseEntity.ok(salesService.getAllOrders());
    }

    @GetMapping("/orders/{id}")
    @Operation(summary = "Obtener orden por ID")
    public ResponseEntity<ServiceOrderResponse> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(salesService.getOrder(id));
    }

    @PutMapping("/orders/{id}/status")
    @Operation(summary = "Actualizar estado de orden")
    public ResponseEntity<ServiceOrderResponse> updateStatus(@PathVariable Long id,
                                                              @Valid @RequestBody OrderStatusRequest request) {
        return ResponseEntity.ok(salesService.updateStatus(id, request));
    }

    @GetMapping("/active-orders")
    @Operation(summary = "Órdenes activas (no entregadas)")
    public ResponseEntity<List<ServiceOrderResponse>> getActiveOrders() {
        return ResponseEntity.ok(salesService.getActiveOrders());
    }

    @GetMapping("/orders/{id}/ticket")
    @Operation(summary = "Generar ticket con código de barras")
    public ResponseEntity<TicketResponse> getTicket(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.generateTicket(id));
    }

    @DeleteMapping("/orders/{id}")
    @Operation(summary = "Eliminar orden")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        salesService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
