# Decisiones Técnicas

## 1. Context path `/api` en backend

**Decisión:** `server.servlet.context-path: /api` en `application.yml`.

**Por qué:** Permite que los controllers usen `@RequestMapping("/auth")` sin repetir `/api` y que el proxy del frontend (que envía `/api/*`) coincida directamente. Aísla la API del root del servidor.

**Descartado:** Usar `/api` como prefijo en cada `@RequestMapping`. Hubiera duplicado el string y dificultado cambios de ruta global.

---

## 2. DTOs como Java `record` en lugar de Lombok

**Decisión:** Los DTOs son `record` de Java (sin Lombok). Las entidades usan Lombok (`@Data @Builder @NoArgsConstructor @AllArgsConstructor`).

**Por qué:** Los `record` son inmutables por diseño, tienen `equals`/`hashCode`/`toString` gratis sin librerías. Las entidades JPA necesitan mutabilidad para que Hibernate funcione, y `@Builder` es útil para construir objetos complejos.

---

## 3. Standalone components + bootstrapApplication en Angular

**Decisión:** Sin `NgModule`. `bootstrapApplication(AppComponent, appConfig)` en `main.ts`.

**Por qué:** Angular 17+ promueve standalone como default. Reduce boilerplate y dependencies.

---

## 4. Proxy de desarrollo vs nginx en producción

**Decisión:** Desarrollo usa `proxy.conf.json` con `ng serve`. Producción usa `nginx.conf` que sirve el SPA y proxea `/api` al backend.

**Por qué:** El proxy de `ng serve` simplifica el desarrollo (sin CORS issues). Nginx es necesario en producción para servir archivos estáticos y enrutar la API.

**Nota:** `environment.ts` define `apiUrl` pero **no se usa** — todas las llamadas son relativas a `/api/...` y el proxy resuelve el destino.

---

## 5. `users` y `reports` restringidos a ADMIN

**Decisión:** En `SecurityConfig`, `/users/**` y `/reports/**` requieren `hasRole("ADMIN")`.

**Por qué:** Los cajeros solo deben gestionar ventas y clientes, no crear usuarios ni ver reportes globales.

---

## 6. `reports` module sin entidad propia

**Decisión:** `modules/reports/` no tiene `model/`. Consulta directamente los repositorios de `sales` y `expenses`.

**Por qué:** Los reportes son consultas agregadas sobre datos existentes, no una entidad nueva.

**Pendiente:** `ReportService.getTopProducts()` y `getTopClients()` retornan listas vacías — son stubs sin implementar.

---

## 7. JwtUtil con constructor explícito + @Value

**Decisión:** `JwtUtil` no usa `@RequiredArgsConstructor` sino constructor explícito con parámetros `@Value`.

**Por qué:** Necesita valores de `application.yml` (`jwt.secret`, `jwt.expiration`) que no son beans de Spring. `@Value` solo funciona en constructores propios o setters, no con Lombok.

---

## 8. TicketService con ZXing para códigos de barras

**Decisión:** Usa `ZXing 3.5.3` para generar código de barras Code 128 en base64 embebido en el HTML del ticket.

**Por qué:** ZXing es la biblioteca estándar de generación de barcodes en Java. Code 128 se usó por su densidad y soporte alfanumérico.

---

## 9. ddl-auto: update en lugar de migraciones

**Decisión:** `spring.jpa.hibernate.ddl-auto: update` en todos los ambientes.

**Por qué:** Simplifica el desarrollo inicial. No hay migraciones Flyway/Liquibase.

**Riesgo:** Cambios destructivos en producción si se modifica el esquema. Se espera migrar a Flyway en el futuro.
