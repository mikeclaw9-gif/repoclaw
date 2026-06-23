package com.pventabase.login.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class LoginResponseDTO {

    private String token;
    private String tipo;
    private String email;
    private String nombre;
    private String apellido;
    private String rol;
}
