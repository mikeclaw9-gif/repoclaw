package com.pventabase.usuarios.service;

import com.pventabase.common.dto.PagedResponse;
import com.pventabase.common.exception.DuplicateResourceException;
import com.pventabase.common.exception.ResourceNotFoundException;
import com.pventabase.usuarios.dto.UsuarioRequestDTO;
import com.pventabase.usuarios.dto.UsuarioResponseDTO;
import com.pventabase.usuarios.entity.Usuario;
import com.pventabase.usuarios.mapper.UsuarioMapper;
import com.pventabase.usuarios.repository.UsuarioRepository;
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
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    @Transactional(readOnly = true)
    public PagedResponse<UsuarioResponseDTO> findAll(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Usuario> usuarioPage = usuarioRepository.findAll(pageable);
        return PagedResponse.from(usuarioPage.map(usuarioMapper::toResponseDto));
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO findById(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
        return usuarioMapper.toResponseDto(usuario);
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO findByEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));
        return usuarioMapper.toResponseDto(usuario);
    }

    public UsuarioResponseDTO create(UsuarioRequestDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("El email " + dto.getEmail() + " ya está registrado");
        }
        Usuario usuario = usuarioMapper.toEntity(dto);
        usuario.setRol(com.pventabase.usuarios.entity.RolUsuario.ROLE_USER);
        usuario = usuarioRepository.save(usuario);
        return usuarioMapper.toResponseDto(usuario);
    }

    public UsuarioResponseDTO update(Long id, UsuarioRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
        if (usuarioRepository.existsByEmailAndIdNot(dto.getEmail(), id)) {
            throw new DuplicateResourceException("El email " + dto.getEmail() + " ya está registrado por otro usuario");
        }
        usuarioMapper.updateEntity(dto, usuario);
        usuario = usuarioRepository.save(usuario);
        return usuarioMapper.toResponseDto(usuario);
    }

    public void delete(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario", id);
        }
        usuarioRepository.deleteById(id);
    }

    public UsuarioResponseDTO toggleActivo(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
        usuario.setActivo(!usuario.getActivo());
        usuario = usuarioRepository.save(usuario);
        return usuarioMapper.toResponseDto(usuario);
    }
}
