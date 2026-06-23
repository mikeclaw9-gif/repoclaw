package com.pventabase.inventario.entity;

import com.pventabase.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "producto")
@Getter
@Setter
public class Producto extends BaseEntity {

    @Column(name = "codigo", nullable = false, length = 50, unique = true)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "precio_compra", nullable = false, precision = 19, scale = 2)
    private BigDecimal precioCompra;

    @Column(name = "precio_venta", nullable = false, precision = 19, scale = 2)
    private BigDecimal precioVenta;

    @Column(name = "existencia", nullable = false)
    private Integer existencia = 0;

    @Column(name = "imagen")
    private String imagen;
}
