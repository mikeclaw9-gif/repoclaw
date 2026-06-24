# Flujo de Trabajo

## Pasos para hacer un cambio

1. **Entender el contexto**
   - Leer `AGENTS.md` (backend y frontend).
   - Leer `docs/contexto/*.md`.
   - Explorar los archivos del módulo involucrado con `codegraph_explore` o lectura directa.

2. **Backend: crear/editar un módulo**
   - Seguir estructura: `model/`, `dto/`, `repository/`, `service/`, `controller/`.
   - Si la entidad no existe aún, crearla con Lombok (`@Data @Builder @NoArgsConstructor @AllArgsConstructor`).
   - DTOs como `record`. Request DTOs con validación Jakarta.
   - Repository extiende `JpaRepository`.
   - Service con `@RequiredArgsConstructor` y `@Transactional` solo si modifica múltiples entidades.
   - Controller con `@RequiredArgsConstructor` y `@RequestMapping("/ruta")`.
   - Registrar rutas públicas o protegidas en `SecurityConfig` si es necesario.

3. **Frontend: crear/editar un módulo**
   - Crear `modulo.routes.ts` con lazy route.
   - Crear `modulo.component.ts` standalone.
   - Si necesita servicios HTTP, agregar métodos en `api.service.ts`.
   - Si necesita modelos, definir interfaces en `core/models/auth.models.ts`.
   - Registrar ruta en `app.routes.ts`.

4. **Verificar**
   - `./mvnw clean package -DskipTests` (backend compila).
   - `ng build` (frontend compila).
   - `docker compose up` (integración).

5. **Commit**
   - `git add` solo los archivos intencionados. No commitear secretos.
   - Mensaje descriptivo en español o inglés (el proyecto usa ambos).

## Checklist de "terminado"

- [ ] Compila sin errores (backend + frontend).
- [ ] No rompe builds existentes.
- [ ] Sigue las convenciones del proyecto (ver `convenciones.md`).
- [ ] No expone secretos/credenciales.
- [ ] Los cambios en API tienen su contraparte en el frontend.
- [ ] `docker compose up` funciona sin errores.

## Deploy

### Desarrollo local
```bash
# Backend
cd laundry-pos-backend
docker compose up

# Frontend (otra terminal)
cd laundry-pos-frontend
ng serve --host 0.0.0.0
```

### Producción (Docker)
```bash
cd laundry-pos-backend
docker compose up -d

cd laundry-pos-frontend
docker compose up -d
```

Actualmente **no hay CI/CD configurado**. El deploy es manual vía Docker Compose.

## Puerto y URLs

| Servicio | Puerto | URL |
|---|---|---|
| Backend (host) | 8085 | `http://localhost:8085/api` |
| Frontend (dev) | 4200 | `http://localhost:4200` |
| PostgreSQL | 5432 | `localhost:5432` |
| Redis | 6379 | `localhost:6379` |
| Swagger UI | — | `http://localhost:8085/api/swagger-ui.html` |
