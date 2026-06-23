package com.pventabase.inventario.service;

import com.pventabase.common.dto.PagedResponse;
import com.pventabase.common.exception.DuplicateResourceException;
import com.pventabase.common.exception.ResourceNotFoundException;
import com.pventabase.inventario.dto.ProductoRequestDTO;
import com.pventabase.inventario.dto.ProductoResponseDTO;
import com.pventabase.inventario.entity.Producto;
import com.pventabase.inventario.mapper.ProductoMapper;
import com.pventabase.inventario.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;

    @Transactional(readOnly = true)
    public PagedResponse<ProductoResponseDTO> findAll(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Producto> productoPage = productoRepository.findAll(pageable);
        return PagedResponse.from(productoPage.map(productoMapper::toResponseDto));
    }

    @Transactional(readOnly = true)
    public ProductoResponseDTO findById(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));
        return productoMapper.toResponseDto(producto);
    }

    @Transactional(readOnly = true)
    public ProductoResponseDTO findByCodigo(String codigo) {
        Producto producto = productoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con código: " + codigo));
        return productoMapper.toResponseDto(producto);
    }

    public ProductoResponseDTO create(ProductoRequestDTO dto) {
        if (productoRepository.existsByCodigo(dto.getCodigo())) {
            throw new DuplicateResourceException("El código " + dto.getCodigo() + " ya existe");
        }
        Producto producto = productoMapper.toEntity(dto);
        producto = productoRepository.save(producto);
        return productoMapper.toResponseDto(producto);
    }

    public ProductoResponseDTO update(Long id, ProductoRequestDTO dto) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));
        if (productoRepository.existsByCodigoAndIdNot(dto.getCodigo(), id)) {
            throw new DuplicateResourceException("El código " + dto.getCodigo() + " ya existe en otro producto");
        }
        productoMapper.updateEntity(dto, producto);
        producto = productoRepository.save(producto);
        return productoMapper.toResponseDto(producto);
    }

    public void delete(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto", id);
        }
        productoRepository.deleteById(id);
    }

    public ProductoResponseDTO toggleActivo(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));
        producto.setActivo(!producto.getActivo());
        producto = productoRepository.save(producto);
        return productoMapper.toResponseDto(producto);
    }
}
