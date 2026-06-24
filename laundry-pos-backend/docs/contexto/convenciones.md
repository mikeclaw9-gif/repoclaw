# Convenciones

## Backend

### DTOs
- `record` de Java, **sin Lombok**.
- Request DTOs llevan `@NotBlank` / `@NotNull` / `@Positive` / `@Size` de Jakarta Validation.
- Response DTOs **no tienen validación**.

### Entidades
- `@Data @Builder @NoArgsConstructor @AllArgsConstructor` (Lombok).
- `@Table(name = "snake_case_plural")`, `@Column(name = "snake_case")`.
- Enums con valores en español: `OrderStatus { PENDIENTE, EN_PROCESO, LISTO, ENTREGADO }`.
- IDs: `Long` generado automáticamente (`@GeneratedValue(strategy = IDENTITY)`).

### Inyección de dependencias
- `@RequiredArgsConstructor` en services, controllers y config. **Sin `@Autowired`**.
- Excepción: `JwtUtil` usa constructor explícito con `@Value` (necesita valores de properties).

### Servicios
- `@Transactional` solo en métodos de escritura que modifican múltiples entidades.
- Lanzar `ResourceNotFoundException` con `orElseThrow()`.
- Lanzar `IllegalArgumentException` para validaciones de negocio.

### API
- `@RequestMapping("/modulo")` sin prefijo `/api` (el context path ya está en `application.yml`).
- Endpoints públicos: `/auth/**`, `/health`, `/actuator/**`, `/swagger-ui.html`, `/swagger-ui/**`, `/v3/api-docs/**`.
- `/users/**` y `/reports/**` solo `ROLE_ADMIN`.
- CSRF deshabilitado, sesión STATELESS.

### Excepciones
- `ResourceNotFoundException` → 404
- `BadCredentialsException` → 401
- `MethodArgumentNotValidException` → 400 (con mapa de errores por campo)
- `HttpRequestMethodNotSupportedException` → 405
- `Exception` → 500
- Todas retornan `ErrorResponse(int status, String message, LocalDateTime timestamp)`.

### Estilo de código
- **Sin comentarios** (solo anotaciones Swagger).
- `var` para tipos locales obvios.
- Stream API con `.toList()`.
- Mensajes de error en español, identificadores en inglés.

## Frontend

### Componentes
- Todos `standalone: true`. No hay `NgModule`.
- Template inline (sin archivos `.html` separados).
- Estilos inline en `styles: [...]` (sin archivos CSS separados).

### Routing
- Lazy loading por módulo con `loadChildren()` / `loadComponent()`.
- `authGuard` protege todas las rutas excepto `/auth`.

### Servicios
- `@Injectable({ providedIn: 'root' })`.
- Métodos que retornan `Observable<T>`.
- `ApiService` centraliza todas las llamadas HTTP. `AuthService` y `SalesService` son auxiliares especializados.

### Interceptores / Guards
- Funciones exportadas (no clases): `authInterceptor` (`HttpInterceptorFn`), `authGuard`.

### Estado
- UI state local en componentes (no NgRx/Store). RxJS solo para polling (dashboard).

### Naming
- Archivos: `kebab-case` (auth.service.ts, login.component.ts).
- Clases: `PascalCase`.
- Métodos/variables: `camelCase`.
- Rutas: `camelCase` en backend (`/cash-closure` en frontend, `/cash-closure` en backend también).

## Commits
- Sin convención fija detectada. Commits recientes:
  - `2e3552fb Standardize backend port to 8085 and update Docker infrastructure`

## Prohibido
- `@Autowired` (usar constructor injection).
- Clases con `@Controller` + `@ResponseBody` (usar `@RestController`).
- `@RequestMapping` anotado con `/api/` (el context-path lo agrega).
- Archivos HTML/CSS separados en frontend (todo inline en el componente).
- jQuery o manipulación directa del DOM.
