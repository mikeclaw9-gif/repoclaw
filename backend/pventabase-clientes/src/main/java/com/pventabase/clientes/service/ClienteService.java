package com.pventabase.clientes.service;

import com.pventabase.common.dto.PagedResponse;
import com.pventabase.common.exception.DuplicateResourceException;
import com.pventabase.common.exception.ResourceNotFoundException;
import com.pventabase.clientes.dto.ClienteRequestDTO;
import com.pventabase.clientes.dto.ClienteResponseDTO;
import com.pventabase.clientes.entity.Cliente;
import com.pventabase.clientes.mapper.ClienteMapper;
import com.pventabase.clientes.repository.ClienteRepository;
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
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    @Transactional(readOnly = true)
    public PagedResponse<ClienteResponseDTO> findAll(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Cliente> clientePage = clienteRepository.findAll(pageable);
        return PagedResponse.from(clientePage.map(clienteMapper::toResponseDto));
    }

    @Transactional(readOnly = true)
    public ClienteResponseDTO findById(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", id));
        return clienteMapper.toResponseDto(cliente);
    }

    @Transactional(readOnly = true)
    public ClienteResponseDTO findByEmail(String email) {
        Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con email: " + email));
        return clienteMapper.toResponseDto(cliente);
    }

    public ClienteResponseDTO create(ClienteRequestDTO dto) {
        if (clienteRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("El email " + dto.getEmail() + " ya está registrado");
        }
        Cliente cliente = clienteMapper.toEntity(dto);
        cliente = clienteRepository.save(cliente);
        return clienteMapper.toResponseDto(cliente);
    }

    public ClienteResponseDTO update(Long id, ClienteRequestDTO dto) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", id));
        if (clienteRepository.existsByEmailAndIdNot(dto.getEmail(), id)) {
            throw new DuplicateResourceException("El email " + dto.getEmail() + " ya está registrado por otro cliente");
        }
        clienteMapper.updateEntity(dto, cliente);
        cliente = clienteRepository.save(cliente);
        return clienteMapper.toResponseDto(cliente);
    }

    public void delete(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cliente", id);
        }
        clienteRepository.deleteById(id);
    }

    public ClienteResponseDTO toggleActivo(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", id));
        cliente.setActivo(!cliente.getActivo());
        cliente = clienteRepository.save(cliente);
        return clienteMapper.toResponseDto(cliente);
    }

    public ClienteResponseDTO marcarEliminado(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", id));
        cliente.setEliminado(true);
        cliente = clienteRepository.save(cliente);
        return clienteMapper.toResponseDto(cliente);
    }
}
