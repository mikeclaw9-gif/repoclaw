package com.pventabase.inventario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class ProductoRequestDTO {

    @NotBlank
    @Size(max = 50)
    private String codigo;

    @NotBlank
    @Size(max = 150)
    private String nombre;

    private String descripcion;

    @PositiveOrZero
    private BigDecimal precioCompra;

    @PositiveOrZero
    private BigDecimal precioVenta;

    @PositiveOrZero
    private Integer existencia;

    private String imagen;
}
