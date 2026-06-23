package com.pventabase.clientes.entity;

import com.pventabase.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "clientes")
@Getter
@Setter
public class Cliente extends BaseEntity {

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido;

    @Column(name = "domicilio", nullable = false, length = 255)
    private String direccion;

    @Column(name = "celular", length = 20)
    private String telefono;

    @Column(name = "correo", nullable = false, length = 150, unique = true)
    private String email;

    @Column(name = "credito", precision = 10, scale = 2)
    private BigDecimal credito;

    @Column(name = "eliminado", nullable = false)
    private Boolean eliminado = false;

    @Column(name = "documento", length = 20)
    private String documento;
}
