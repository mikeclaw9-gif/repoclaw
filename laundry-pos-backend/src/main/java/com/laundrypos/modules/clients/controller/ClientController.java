package com.laundrypos.modules.clients.controller;

import com.laundrypos.modules.clients.dto.ClientRequest;
import com.laundrypos.modules.clients.dto.ClientResponse;
import com.laundrypos.modules.clients.service.ClientService;
import com.laundrypos.modules.sales.model.ServiceOrder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "Gestión de clientes")
public class ClientController {

    private final ClientService clientService;

    @GetMapping
    @Operation(summary = "Listar todos los clientes")
    public ResponseEntity<List<ClientResponse>> findAll() {
        return ResponseEntity.ok(clientService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cliente por ID")
    public ResponseEntity<ClientResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Crear cliente")
    public ResponseEntity<ClientResponse> create(@Valid @RequestBody ClientRequest request) {
        return ResponseEntity.ok(clientService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar cliente")
    public ResponseEntity<ClientResponse> update(@PathVariable Long id, @Valid @RequestBody ClientRequest request) {
        return ResponseEntity.ok(clientService.update(id, request));
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar cliente por teléfono")
    public ResponseEntity<List<ClientResponse>> search(@RequestParam String phone) {
        return ResponseEntity.ok(clientService.search(phone));
    }

    @GetMapping("/{id}/service-history")
    @Operation(summary = "Historial de servicios del cliente")
    public ResponseEntity<List<ServiceOrder>> getServiceHistory(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.getServiceHistory(id));
    }
}
