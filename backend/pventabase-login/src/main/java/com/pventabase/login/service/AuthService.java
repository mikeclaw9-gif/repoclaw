package com.pventabase.login.service;

import com.pventabase.common.exception.DuplicateResourceException;
import com.pventabase.common.exception.InvalidStateException;
import com.pventabase.common.exception.ResourceNotFoundException;
import com.pventabase.login.dto.LoginRequestDTO;
import com.pventabase.login.dto.LoginResponseDTO;
import com.pventabase.login.dto.RegisterRequestDTO;
import com.pventabase.login.security.JwtTokenProvider;
import com.pventabase.usuarios.entity.RolUsuario;
import com.pventabase.usuarios.entity.Usuario;
import com.pventabase.usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequestDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + dto.getEmail()));

        if (!usuario.getActivo()) {
            throw new InvalidStateException("El usuario está desactivado");
        }

        if (!passwordEncoder.matches(dto.getPassword(), usuario.getPassword())) {
            throw new InvalidStateException("Credenciales inválidas");
        }

        String token = jwtTokenProvider.generateToken(usuario.getEmail(), usuario.getRol().name());

        return LoginResponseDTO.builder()
                .token(token)
                .tipo("Bearer")
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .rol(usuario.getRol().name())
                .build();
    }

    public LoginResponseDTO register(RegisterRequestDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("El email " + dto.getEmail() + " ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setEmail(dto.getEmail());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setTelefono(dto.getTelefono());

        if (dto.getRol() != null && !dto.getRol().isEmpty()) {
            usuario.setRol(RolUsuario.valueOf(dto.getRol()));
        } else {
            usuario.setRol(RolUsuario.ROLE_USER);
        }

        usuario = usuarioRepository.save(usuario);

        String token = jwtTokenProvider.generateToken(usuario.getEmail(), usuario.getRol().name());

        return LoginResponseDTO.builder()
                .token(token)
                .tipo("Bearer")
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .rol(usuario.getRol().name())
                .build();
    }
}
