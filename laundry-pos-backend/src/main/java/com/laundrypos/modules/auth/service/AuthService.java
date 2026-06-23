package com.laundrypos.modules.auth.service;

import com.laundrypos.modules.auth.dto.LoginRequest;
import com.laundrypos.modules.auth.dto.LoginResponse;
import com.laundrypos.modules.auth.dto.RefreshTokenRequest;
import com.laundrypos.modules.auth.dto.RegisterRequest;
import com.laundrypos.modules.auth.model.User;
import com.laundrypos.modules.auth.repository.UserRepository;
import com.laundrypos.shared.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        } catch (Exception e) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        var user = userRepository.findByUsername(request.username())
            .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));

        if (!user.isActive()) {
            throw new BadCredentialsException("Usuario desactivado");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        return new LoginResponse(token, token, user.getUsername(), user.getRole().name());
    }

    public LoginResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("El usuario ya existe");
        }

        var user = User.builder()
            .username(request.username())
            .passwordHash(passwordEncoder.encode(request.password()))
            .role(User.Role.valueOf(request.role()))
            .build();

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        return new LoginResponse(token, token, user.getUsername(), user.getRole().name());
    }

    public LoginResponse refreshToken(RefreshTokenRequest request) {
        if (!jwtUtil.validateToken(request.refreshToken())) {
            throw new IllegalArgumentException("Token inválido");
        }

        String username = jwtUtil.getUsernameFromToken(request.refreshToken());
        var user = userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        return new LoginResponse(token, token, user.getUsername(), user.getRole().name());
    }

    public boolean validateSession(String token) {
        return jwtUtil.validateToken(token);
    }
}
