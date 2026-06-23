# SPEC.md - Sistema de Punto de Venta para Lavandería

## 1. Descripción General del Proyecto

Sistema integral de punto de venta (POS) diseñado específicamente para la administración y control de una lavandería. La plataforma permite gestionar servicios de lavado, venta de productos complementarios, inventario, clientes, proveedores y comunicaciones con clientes vía WhatsApp.

### 1.1 Alcance del Sistema

- Registro y autenticación de usuarios del sistema
- Gestión de tickets de servicio con impresión
- Administración de clientes frecuentes con historial
- Control de proveedores y compras
- Punto de venta integrado para servicios y productos, el servicio de lavado de ropa se cobra por kilo
- Sistema de inventario con control de stock
- Módulo para registrar gastos
- Módulo de reportes y análisis de ventas y gastos
- Notificaciones automatizadas por WhatsApp o telegram
- Monitoreo de estado del servidor
- Para el frontend implementar un tema oscuro por default con la posibilidad de cambiarlo a claro.
- La interfaz grafica del frontend debe ser moderna y minimalista.

---

## 2. Stack Tecnológico

### 2.1 Backend
- **Lenguaje:** Java 17+
- **Framework:** Spring Boot 3.x
- **Base de datos:** PostgreSQL 15+
- **Documentación API:** Swagger/OpenAPI 3.0
- **ORM:** Spring Data JPA / Hibernate
- **Seguridad:** Spring Security con JWT
- **Mensajería:** API de WhatsApp Business (Twilio o similar) o telegram

### 2.2 Frontend
- **Framework:** Angular 17+
- **UI Components:** Angular Material o PrimeNG
- **Estado:** RxJS / NgRx
- **HTTP Client:** Angular HttpClient
- **Impresión:** Thermal Printer API / ngx-thermal-printer

### 2.3 Infraestructura
- **Contenedores:** Docker + Docker Compose
- **Servidor:** Embedded Tomcat (Spring Boot)
- **Build Tools:** Maven (Backend) / Angular CLI (Frontend)

---

## 3. Estructura del Proyecto

### 3.1 Proyecto Backend (`laundry-pos-backend`)

laundry-pos-backend/
├── src/main/java/com/laundrypos/
│ ├── LaundryPosApplication.java
│ ├── config/
│ │ ├── SecurityConfig.java
│ │ ├── SwaggerConfig.java
│ │ └── CorsConfig.java
│ ├── modules/
│ │ ├── auth/
│ │ │ ├── controller/
│ │ │ ├── service/
│ │ │ ├── repository/
│ │ │ ├── dto/
│ │ │ └── model/
│ │ ├── users/
│ │ │ ├── controller/
│ │ │ ├── service/
│ │ │ ├── repository/
│ │ │ ├── dto/
│ │ │ └── model/
│ │ ├── clients/
│ │ │ ├── controller/
│ │ │ ├── service/
│ │ │ ├── repository/
│ │ │ ├── dto/
│ │ │ └── model/
│ │ ├── sales/
│ │ │ ├── controller/
│ │ │ ├── service/
│ │ │ ├── repository/
│ │ │ ├── dto/
│ │ │ └── model/
│ │ ├── inventory/
│ │ │ ├── controller/
│ │ │ ├── service/
│ │ │ ├── repository/
│ │ │ ├── dto/
│ │ │ └── model/
│ │ ├── suppliers/
│ │ │ ├── controller/
│ │ │ ├── service/
│ │ │ ├── repository/
│ │ │ ├── dto/
│ │ │ └── model/
│ │ ├── reports/
│ │ │ ├── controller/
│ │ │ ├── service/
│ │ │ ├── repository/
│ │ │ └── dto/
│ │ └── notifications/
│ │ ├── controller/
│ │ ├── service/
│ │ └── dto/
│ ├── shared/
│ │ ├── exception/
│ │ ├── util/
│ │ └── constant/
│ └── resources/
│ ├── application.yml
│ ├── application-dev.yml
│ └── application-prod.yml
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md

### 3.2 Proyecto Frontend (`laundry-pos-frontend`)

laundry-pos-frontend/
├── src/
│ ├── app/
│ │ ├── core/
│ │ │ ├── services/
│ │ │ ├── guards/
│ │ │ ├── interceptors/
│ │ │ └── models/
│ │ ├── shared/
│ │ │ ├── components/
│ │ │ └── pipes/
│ │ ├── modules/
│ │ │ ├── auth/
│ │ │ ├── dashboard/
│ │ │ ├── pos/
│ │ │ ├── clients/
│ │ │ ├── inventory/
│ │ │ ├── suppliers/
│ │ │ └── reports/
│ │ └── app-routing.module.ts
│ ├── environments/
│ └── assets/
├── Dockerfile
├── angular.json
├── package.json
└── README.md


---

## 4. Módulos del Backend

### 4.1 Módulo de Autenticación (`auth`)
**Responsabilidades:**
- Login con JWT (access token + refresh token)
- Registro inicial de administrador
- Recuperación de contraseña
- Validación de sesiones activas

**Endpoints:**
- `POST /api/auth/login`
- `POST /api/auth/register`
- `POST /api/auth/refresh-token`
- `POST /api/auth/forgot-password`
- `GET /api/auth/validate-session`

### 4.2 Módulo de Usuarios (`users`)
**Responsabilidades:**
- CRUD de usuarios del sistema (cajeros, administradores)
- Gestión de roles y permisos
- Activación/desactivación de cuentas

**Roles:**
- `ROLE_ADMIN`: Acceso total al sistema
- `ROLE_CASHIER`: Punto de venta y consultas básicas

**Endpoints:**
- `GET /api/users`
- `POST /api/users`
- `PUT /api/users/{id}`
- `DELETE /api/users/{id}`
- `PATCH /api/users/{id}/status`

### 4.3 Módulo de Clientes (`clients`)
**Responsabilidades:**
- Registro de clientes frecuentes
- Historial de servicios por cliente
- Sistema de puntos o fidelización (opcional)
- Búsqueda por teléfono, nombre o email

**Endpoints:**
- `GET /api/clients`
- `GET /api/clients/{id}`
- `POST /api/clients`
- `PUT /api/clients/{id}`
- `GET /api/clients/search?phone=`
- `GET /api/clients/{id}/service-history`

### 4.4 Módulo de Ventas/POS (`sales`)
**Responsabilidades:**
- Registro de servicios de lavandería (lavado, secado, planchado)
- Venta de productos adicionales en la misma transacción
- Generación de ticket con número de orden
- Cálculo automático de totales
- Estados del servicio: pendiente, en proceso, listo, entregado
- Impresión de ticket
- El ticket debe contar con un codigo de barras que representa el numero de ticket
- El ticket debe mostrar el numero de ticket con letras mas grandes que el resto
- Se deben imprimir 2 tickets, uno para el cliente y una copia para rastreo de las prendas.

**Endpoints:**
- `POST /api/sales/service-order`
- `GET /api/sales/orders`
- `GET /api/sales/orders/{id}`
- `PUT /api/sales/orders/{id}/status`
- `POST /api/sales/orders/{id}/add-products`
- `GET /api/sales/active-orders`
- `GET /api/sales/ticket/{id}`

### 4.5 Módulo de Inventario (`inventory`)
**Responsabilidades:**
- Catálogo de productos para venta
- Control de entradas y salidas de stock
- Alertas de stock bajo
- Categorización de productos
- Gestión de precios

**Endpoints:**
- `GET /api/inventory/products`
- `POST /api/inventory/products`
- `PUT /api/inventory/products/{id}`
- `GET /api/inventory/stock-movements`
- `POST /api/inventory/stock-entry`
- `POST /api/inventory/stock-exit`
- `GET /api/inventory/low-stock-alerts`

### 4.6 Módulo de Proveedores (`suppliers`)
**Responsabilidades:**
- Registro de proveedores de insumos
- Historial de compras por proveedor
- Información de contacto y productos suministrados

**Endpoints:**
- `GET /api/suppliers`
- `POST /api/suppliers`
- `PUT /api/suppliers/{id}`
- `GET /api/suppliers/{id}/purchase-history`

### 4.7 Módulo de Reportes (`reports`)
**Responsabilidades:**
- Ventas por día, semana, mes y año
- Productos más vendidos
- Servicios más solicitados
- Clientes frecuentes
- Reportes exportables (PDF, Excel)

**Endpoints:**
- `GET /api/reports/sales/daily?date=`
- `GET /api/reports/sales/weekly?start=&end=`
- `GET /api/reports/sales/monthly?month=&year=`
- `GET /api/reports/sales/yearly?year=`
- `GET /api/reports/top-products`
- `GET /api/reports/top-clients`
- `GET /api/reports/export?type=&format=`

### 4.8 Módulo de Notificaciones (`notifications`)
**Responsabilidades:**
- Envío de mensajes WhatsApp cuando el servicio está listo
- Plantillas de mensajes personalizables
- Historial de notificaciones enviadas

**Endpoints:**
- `POST /api/notifications/send-ready/{orderId}`
- `GET /api/notifications/templates`
- `PUT /api/notifications/templates/{id}`

### 4.9 Health Check
**Endpoint:**
- `GET /api/health`
  - Response: `{ "status": "UP", "timestamp": "2024-01-01T00:00:00", "version": "1.0.0" }`

---

## 5. Modelo de Datos Principal

### 5.1 Tablas Esenciales
- `users` (id, username, password_hash, role, active, created_at)
- `clients` (id, name, phone, email, loyalty_points, created_at)
- `service_types` (id, name, price_per_kg, estimated_time_minutes)
- `service_orders` (id, client_id, user_id, total_weight, total_amount, status, created_at, ready_at)
- `order_items` (id, order_id, service_type_id, weight, price)
- `products` (id, name, category, price, stock, min_stock, supplier_id)
- `order_products` (id, order_id, product_id, quantity, unit_price)
- `suppliers` (id, name, contact_name, phone, email, address)
- `inventory_movements` (id, product_id, type, quantity, reason, created_at)
- `notifications` (id, client_id, order_id, type, sent_at, status)

---

## 6. Requisitos Específicos del Frontend

### 6.1 Pantalla Principal (Dashboard)
- Mostrar estado del servidor (consumiendo `/api/health`)
- Indicador visual: verde (online) / rojo (offline)
- Resumen de órdenes activas
- Accesos rápidos a módulos principales

### 6.2 Pantalla de Punto de Venta (POS)
- Formulario unificado que permita:
  - Seleccionar tipo de servicio de lavandería
  - Ingresar peso de la ropa
  - Agregar productos adicionales (jabón, suavizante, etc.)
  - Buscar/registrar cliente
  - Calcular total automáticamente
  - Generar e imprimir ticket
- Diseño optimizado para pantalla táctil

### 6.3 Módulo de Clientes
- Tabla con búsqueda y filtros
- Formulario de registro/edición
- Vista de historial de servicios

### 6.4 Módulo de Inventario
- Lista de productos con indicadores de stock
- Alertas visuales para stock bajo
- Registro de entradas/salidas

### 6.5 Módulo de Reportes
- Selector de período (día/semana/mes/año)
- Gráficos de barras/líneas (Chart.js o similar)
- Botón de exportar PDF/Excel

---

## 7. Docker y Despliegue

### 7.1 Dockerfile Backend
```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]


8. Documentación con Swagger
8.1 Configuración

    URL de Swagger UI: http://localhost:8080/api/swagger-ui.html

    API Docs JSON: http://localhost:8080/api/v3/api-docs

    Incluir descripción de todos los endpoints con ejemplos

    Agrupar por módulos (tags)

    Documentar esquemas de request/response

    Incluir autorización JWT en Swagger UI

9. Instrucciones README.md
9.1 Backend README.md Debe Incluir:

    Requisitos: Java 17, Maven 3.8+, PostgreSQL 15

    Compilación: mvn clean package -DskipTests

    Ejecución local: mvn spring-boot:run -Dspring-boot.run.profiles=dev

    Docker build: docker build -t laundry-backend .

    Docker run: docker run -p 8080:8080 laundry-backend

    Docker Compose: docker-compose up -d

    Swagger: http://localhost:8080/api/swagger-ui.html

    Variables de entorno configurables

9.2 Frontend README.md Debe Incluir:

    Requisitos: Node 18+, Angular CLI 17+

    Instalación: npm install

    Ejecución desarrollo: ng serve (http://localhost:4200)

    Compilación: ng build --prod

    Docker build: docker build -t laundry-frontend .

    Docker run: docker run -p 80:80 laundry-frontend

    Docker Compose: docker-compose up -d

    Configuración de environment para API URL

10. Seguridad

    Todas las APIs protegidas con JWT (excepto /api/auth/* y /api/health)

    Tokens con expiración configurable

    CORS configurado para el dominio del frontend

    Contraseñas hasheadas con BCrypt

    Validación de roles en cada endpoint

    HTTPS en producción

11. Consideraciones Adicionales

    El sistema debe ser responsive para tablets (uso principal en mostrador)

    Soporte para impresoras térmicas de tickets

    Integración con API de WhatsApp Business (requiere cuenta verificada) o Telegram

    Logs centralizados para debugging

    Pruebas unitarias con JUnit y Mockito (cobertura mínima 70%)

    El frontend debe mostrar mensajes de error descriptivos del backend

    Manejo de excepciones global con @ControllerAdvice

    Paginación en endpoints de listado

12. Entregables

    Código fuente del backend (laundry-pos-backend/)

    Código fuente del frontend (laundry-pos-frontend/)

    Archivo docker-compose.yml

    README.md por cada proyecto

    Colección de Postman o archivo de Swagger exportado

    Script SQL inicial con datos de prueba

    Este `SPEC.md` proporciona una guía completa y detallada para implementar el sistema de punto de venta para lavandería, cubriendo todos los requisitos solicitados, la estructura modular, configuración Docker y documentación necesaria.