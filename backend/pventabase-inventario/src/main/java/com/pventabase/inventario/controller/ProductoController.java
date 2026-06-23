package com.pventabase.inventario.controller;

import com.pventabase.common.dto.PagedResponse;
import com.pventabase.inventario.dto.ProductoRequestDTO;
import com.pventabase.inventario.dto.ProductoResponseDTO;
import com.pventabase.inventario.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    public ResponseEntity<PagedResponse<ProductoResponseDTO>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        return ResponseEntity.ok(productoService.findAll(page, size, sortBy, sortDir));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.findById(id));
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<ProductoResponseDTO> findByCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(productoService.findByCodigo(codigo));
    }

    @PostMapping
    public ResponseEntity<ProductoResponseDTO> create(@Valid @RequestBody ProductoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> update(@PathVariable Long id, @Valid @RequestBody ProductoRequestDTO dto) {
        return ResponseEntity.ok(productoService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-activo")
    public ResponseEntity<ProductoResponseDTO> toggleActivo(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.toggleActivo(id));
    }

    @PostMapping(value = "/imagen", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> subirImagen(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "nombreProducto", required = false) String nombreProducto) throws IOException {

        String uploadDir = System.getProperty("user.dir") + File.separator + "imagenes";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String extension = "";
        String originalName = file.getOriginalFilename();
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }

        String filename;
        if (nombreProducto != null && !nombreProducto.isBlank()) {
            String nombreSinEspacios = nombreProducto.replaceAll("\\s+", "");
            int aleatorio = new Random().nextInt(90000) + 10000;
            filename = nombreSinEspacios + aleatorio + extension;
        } else {
            filename = UUID.randomUUID().toString() + extension;
        }

        Path filePath = Path.of(uploadDir, filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return ResponseEntity.ok(Map.of("ruta", "/imagenes/" + filename, "nombre", filename));
    }
}
