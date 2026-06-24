# Errores Conocidos

## Backend

### `getTopProducts()` y `getTopClients()` retornan vacío

`ReportService` tiene stubs sin implementar. Si el frontend consume estos endpoints, recibirá listas vacías en lugar de datos.

**Archivo:** `modules/reports/service/ReportService.java`

---

### `NotificationService.updateTemplate()` es no-op

El método recibe el request pero no persiste nada. Solo retorna un `NotificationTemplateRequest` mockeado.

**Archivo:** `modules/notifications/service/NotificationService.java`

---

### `NotificationService.sendReadyNotification()` no envía mensajes reales

Solo persiste un registro en DB con status `PENDING`. No hay integración real con WhatsApp ni Telegram.

**Archivo:** `modules/notifications/service/NotificationService.java`

---

### `SupplierService.getPurchaseHistory()` usa filtro post-query

En vez de una consulta JPQL, trae todos los productos del proveedor y filtra en Java con `stream().filter()`. Ineficiente con muchos datos.

**Archivo:** `modules/suppliers/service/SupplierService.java`

---

### `CashClosureService.create()` no valida cierre abierto existente

Si ya hay un corte abierto, `create()` crea otro sin verificar. Pueden existir múltiples cortes abiertos simultáneamente.

**Archivo:** `modules/cash_closure/service/CashClosureService.java`

---

### `application-prod.yml` con datasource por variables de entorno

En `docker` profile (usado en Docker Compose), las variables `SPRING_DATASOURCE_URL` se setean en el `docker-compose.yml`. Pero `application-prod.yml` usa `${DATASOURCE_URL}`, no `${SPRING_DATASOURCE_URL}`. Si se activa el perfil `prod` en Docker, no encontrará las variables.

---

### WARNING: `PostgreSQLDialect` no necesita especificarse

Hibernate 6 lo detecta automáticamente. La propiedad `hibernate.dialect` en `application.yml` es redundante.

---

### SecurityConfig usa `.cors().and()` deprecated

En Spring Security 6.2+ el DSL cambió. El método `.cors().and()` está marcado para eliminación. Debería ser `.cors(Customizer.withDefaults())`.

**Archivo:** `modules/config/SecurityConfig.java`

---

## Frontend

### API URL de `environment.ts` no se usa

`environment.apiUrl` está definida pero nunca referenciada en `ApiService`. Todas las rutas son hardcodeadas como `/api/...`. Cambiar el backend de puerto requiere actualizar `proxy.conf.json`, no `environment.ts`.

---

### 502 Bad Gateway en login

El proxy de Angular dev server puede fallar si `localhost` resuelve a IPv6 (`::1`) y el backend solo escucha en IPv4. Solución: usar `127.0.0.1` en `proxy.conf.json` y agregar `"changeOrigin": true`.

---

### OTS parsing error con WOFF2

El navegador muestra `OTS parsing error: Failed to convert WOFF 2.0 font to SFNT` al cargar fuentes de `@fontsource/inter`. Es un warning visual que no bloquea funcionalidad. Ocurre cuando el servidor de desarrollo sirve `.woff2` con Content-Type incorrecto.

---

### No hay tests

`src/test/` no existe en backend ni frontend. No hay cobertura automatizada.

---

### `reports.component.ts.bak` duplicado

Hay un archivo `.bak` idéntico al original en `modules/reports/`. No debería estar en el repositorio.
