CREATE TABLE producto (
    id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT,
    precio_compra NUMERIC(19,2) NOT NULL CHECK (precio_compra >= 0),
    precio_venta NUMERIC(19,2) NOT NULL CHECK (precio_venta >= 0),
    existencia INTEGER NOT NULL DEFAULT 0 CHECK (existencia >= 0),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE INDEX idx_producto_codigo ON producto(codigo);
CREATE INDEX idx_producto_activo ON producto(activo);
