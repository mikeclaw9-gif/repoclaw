CREATE TABLE clientes (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    domicilio VARCHAR(255) NOT NULL,
    celular VARCHAR(20),
    correo VARCHAR(150) NOT NULL UNIQUE,
    credito NUMERIC(10,2),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    eliminado BOOLEAN NOT NULL DEFAULT FALSE,
    documento VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE INDEX idx_clientes_correo ON clientes(correo);
CREATE INDEX idx_clientes_activo ON clientes(activo);
CREATE INDEX idx_clientes_eliminado ON clientes(eliminado);
