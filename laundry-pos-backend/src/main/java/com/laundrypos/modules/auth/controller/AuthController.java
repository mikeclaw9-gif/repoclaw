package com.laundrypos.modules.auth.controller;

import com.laundrypos.modules.auth.dto.LoginRequest;
import com.laundrypos.modules.auth.dto.LoginResponse;
import com.laundrypos.modules.auth.dto.RefreshTokenRequest;
import com.laundrypos.modules.auth.dto.RegisterRequest;
import com.laundrypos.modules.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints de autenticación y registro")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar usuario")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refrescar token")
    public ResponseEntity<LoginResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @GetMapping("/validate-session")
    @Operation(summary = "Validar sesión activa")
    public ResponseEntity<Boolean> validateSession(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(authService.validateSession(token.replace("Bearer ", "")));
    }
}
