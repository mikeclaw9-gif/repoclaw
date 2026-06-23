# AGENTS.md — Laundry POS Backend

Backend API REST para POS de lavandería. Spring Boot 3.2.0 / Java 17 / PostgreSQL.

---

## Stack

| Dependencia | Versión |
|---|---|
| Spring Boot | 3.2.0 |
| jjwt | 0.12.3 |
| Springdoc OpenAPI | 2.3.0 |
| Lombok | (última) |
| ZXing (barcodes) | 3.5.3 |

---

## Módulos existentes (verificar antes de crear uno nuevo)

Cada módulo sigue `model/`, `dto/`, `repository/`, `service/`, `controller/`. La excepción es `reports`, que no tiene entidad JPA propia (solo DTOs + servicio que consulta otros módulos).

| Módulo | Route | Paquete |
|---|---|---|
| Auth | `/auth` | `modules.auth` |
| Users | `/users` | `modules.users` (reusa `User` de auth) |
| Clients | `/clients` | `modules.clients` |
| Sales | `/sales` | `modules.sales` |
| Inventory | `/inventory` | `modules.inventory` |
| Suppliers | `/suppliers` | `modules.suppliers` |
| Reports | `/reports` | `modules.reports` |
| Notifications | `/notifications` | `modules.notifications` |
| Expenses | `/expenses` | `modules.expenses` |
| Services | `/services` | `modules.services` |
| Cash Closure | `/cash-closure` | `modules.cash_closure` |
| Health | `/health` | `modules.auth.controller` |

---

## Convenciones que un agente adivinaría mal

- **DTOs**: Java `record`, sin Lombok. Request tiene `@NotBlank`/`@NotNull`/`@Positive`/`@Size`; Response no tiene validación.
- **Entidades**: `@Data @Builder @NoArgsConstructor @AllArgsConstructor`. `@Table(name = "snake_case_plural")`, `@Column(name = "snake_case")`.
- **Constructor injection**: `@RequiredArgsConstructor` en services/controllers/config. Sin `@Autowired`. Excepción: `JwtUtil` usa constructor explícito con `@Value`.
- **Servicios**: `@Transactional` solo en escritura que modifica múltiples entidades. `orElseThrow(() -> new ResourceNotFoundException(...))`. `IllegalArgumentException` para validación de negocio.
- **API**: Controller `@RequestMapping` solo el nombre del módulo (ej: `/clients`, no `/api/clients`). El context path `/api` ya está en `application.yml`.
- **Seguridad**: JWT Bearer. Endpoints públicos: `/auth/**`, `/health`. Roles: `ROLE_ADMIN`, `ROLE_CASHIER`. `/users/**` y `/reports/**` solo ADMIN. CSRF deshabilitado, STATELESS.
- **Excepciones**: `ResourceNotFoundException` → 404, `BadCredentialsException` → 401, `MethodArgumentNotValidException` → 400, `Exception` → 500. Todas retornan `ErrorResponse(int status, String message, LocalDateTime timestamp)`.
- **Estilo**: Sin comentarios (solo anotaciones Swagger). `var` para locales. Stream API con `.toList()`. Mensajes en español, identificadores en inglés. snake_case en DB. Enums con valores en español (ej: `OrderStatus { PENDIENTE, EN_PROCESO, LISTO, ENTREGADO }`).

---

## Puerto y conexiones

- **Local**: server.port=8082, DB en `localhost:5432` (usuario `miguel`, password `Gallego`, DB `laundry`)
- **Docker**: EXPOSE 8080, docker-compose mapea `8082:8080`, DB interna en `postgres:5432` (usuario `laundry`, password `laundry123`, DB `laundrypos`)
- **Swagger UI**: `http://localhost:8082/api/swagger-ui.html`
- **`ddl-auto: update`** — Hibernate crea/esquemas automáticamente

> Hay dos sets de credenciales DB (dev local vs docker-compose). No commitear credenciales reales.

---

## Comandos

```bash
./mvnw clean package -DskipTests
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
./mvnw test
docker-compose up -d                                    # PostgreSQL + backend
java -jar target/laundry-pos-backend-1.0.0.jar --spring.profiles.active=prod
```

No hay tests aún — `src/test/` está vacío. No hay `.gitignore` ni CI/CD.

---

## Variables de entorno

| Variable | Default |
|---|---|
| `DATASOURCE_URL` / `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/laundrypos` |
| `DATASOURCE_USERNAME` / `SPRING_DATASOURCE_USERNAME` | `laundry` |
| `DATASOURCE_PASSWORD` / `SPRING_DATASOURCE_PASSWORD` | `laundry123` |
| `JWT_SECRET` | `f5bq3MoI4cTwg0pw1yEJ8klHYupJS5zUqQLoe51UDq4=` |
| `JWT_EXPIRATION` | `86400000` |

---

<!-- CODEGRAPH_START -->
## CodeGraph

Hay `.codegraph/` en la raíz. Usar `codegraph_explore` o `codegraph node` ANTES de grep/find/read para entender código. Si no hay `.codegraph/`, ignorar.
<!-- CODEGRAPH_END -->
