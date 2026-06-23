package com.pventabase.clientes.controller;

import com.pventabase.common.dto.PagedResponse;
import com.pventabase.clientes.dto.ClienteRequestDTO;
import com.pventabase.clientes.dto.ClienteResponseDTO;
import com.pventabase.clientes.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping
    public ResponseEntity<PagedResponse<ClienteResponseDTO>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        return ResponseEntity.ok(clienteService.findAll(page, size, sortBy, sortDir));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.findById(id));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ClienteResponseDTO> findByEmail(@PathVariable String email) {
        return ResponseEntity.ok(clienteService.findByEmail(email));
    }

    @PostMapping
    public ResponseEntity<ClienteResponseDTO> create(@Valid @RequestBody ClienteRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> update(@PathVariable Long id, @Valid @RequestBody ClienteRequestDTO dto) {
        return ResponseEntity.ok(clienteService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clienteService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-activo")
    public ResponseEntity<ClienteResponseDTO> toggleActivo(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.toggleActivo(id));
    }

    @PatchMapping("/{id}/marcar-eliminado")
    public ResponseEntity<ClienteResponseDTO> marcarEliminado(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.marcarEliminado(id));
    }
}
