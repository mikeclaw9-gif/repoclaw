# Laundry POS Backend

Sistema de Punto de Venta para Lavandería — Backend API REST

## Requisitos

- Java 17+
- Maven 3.8+
- PostgreSQL 15+

## Compilación

```bash
./mvnw clean package -DskipTests
```

## Ejecución local

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

El servidor corre en `http://localhost:8082/api` (dev local).

## Docker Compose

```bash
docker-compose up -d
```

Inicia PostgreSQL + backend. El backend queda en `http://localhost:8082/api` (mapeo `8082:8080`).

## Docker

```bash
docker build -t laundry-backend .
docker run -p 8082:8080 laundry-backend
```

## Swagger

- Swagger UI: `http://localhost:8082/api/swagger-ui.html`
- API Docs JSON: `http://localhost:8082/api/v3/api-docs`

## Variables de entorno

| Variable | Descripción | Default (dev local) |
|---|---|---|
| `DATASOURCE_URL` / `SPRING_DATASOURCE_URL` | URL de PostgreSQL | `jdbc:postgresql://localhost:5432/laundry` |
| `DATASOURCE_USERNAME` / `SPRING_DATASOURCE_USERNAME` | Usuario BD | `miguel` |
| `DATASOURCE_PASSWORD` / `SPRING_DATASOURCE_PASSWORD` | Contraseña BD | `Gallego` |
| `JWT_SECRET` | Secreto JWT | `f5bq3MoI4cTwg0pw1yEJ8klHYupJS5zUqQLoe51UDq4=` |
| `JWT_EXPIRATION` | Expiración JWT (ms) | `86400000` |

> En Docker Compose se usan credenciales distintas (`laundry`/`laundry123`, DB `laundrypos`).

## Endpoints principales

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/auth/login` | Iniciar sesión |
| POST | `/api/auth/register` | Registrar usuario |
| GET | `/api/health` | Health check |
| GET | `/api/users` | Listar usuarios |
| GET | `/api/clients` | Listar clientes |
| POST | `/api/sales/service-order` | Crear orden de servicio |
| GET | `/api/inventory/products` | Listar productos |
| GET | `/api/suppliers` | Listar proveedores |
| GET | `/api/expenses` | Listar gastos |
| GET | `/api/reports/sales/daily` | Reporte ventas diario |
| POST | `/api/cash-closure/open` | Apertura de caja |
