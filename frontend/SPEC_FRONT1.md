# SPEC_FRONT1 — Especificación Frontend pventafront

> **Documento de especificación arquitectónica.** Describe la estructura, componentes, flujo de datos y configuración del frontend SPA. No es un dump de código sino una guía de diseño e implementación.

---

## 1. DEFINICIÓN DEL PROYECTO

| Atributo | Valor |
|----------|-------|
| Nombre | `pventafront` — SPA de Punto de Venta |
| Versión | 1.0.0 |
| Tech Stack | Vue 3.5 + Quasar 2.17, TypeScript 5.5, Pinia 2.2, Axios 1.7, Vue Router 4.4, jsPDF 4.2 + jspdf-autotable, Sass, Vite (Quasar App Vite 2.1) |
| Puerto dev | 9000 |
| Proxy | `/api` → `http://localhost:8090` (Quasar devServer, evita CORS en desarrollo) |
| Tipo | SPA con hash routing (`createWebHashHistory`) |

### Dependencias principales

- **Runtime:** `vue`, `vue-router`, `pinia`, `axios`, `quasar`, `@quasar/extras` (material-icons), `jspdf`, `jspdf-autotable`
- **Dev:** `@quasar/app-vite`, `typescript`, `sass`, `sass-loader`, `@types/node`

---

## 2. ESTRUCTURA DEL PROYECTO

```
pventafront/
├── index.html                          # Entry point HTML (lang=es)
├── env.d.ts                            # Declaraciones TS para .vue y Quasar
├── package.json                        # Scripts: dev, build
├── quasar.config.js                    # Config Quasar: plugins, proxy, tema, idioma
├── tsconfig.json                       # ES2022, strict, paths @/ → src/
└── src/
    ├── App.vue                         # Raíz: <router-view />
    ├── api/                            # Capa HTTP (Axios)
    │   ├── auth.api.ts                 #   POST /auth/login
    │   ├── producto.api.ts             #   CRUD /productos
    │   ├── usuario.api.ts              #   CRUD /usuarios
    │   └── cliente.api.ts              #   CRUD /clientes
    ├── boot/                           # Archivos de arranque Quasar
    │   └── axios.ts                    #   Instancia Axios, interceptors token/401
    ├── css/                            # Estilos globales
    │   └── app.scss                    #   Reset margin/padding
    ├── layouts/                        # Layouts de Vue Router
    │   └── MainLayout.vue              #   Header + Drawer + RouterView
    ├── pages/                          # Vistas (páginas)
    │   ├── LoginPage.vue               #   Formulario login
    │   ├── DashboardPage.vue           #   Tarjetas de acceso rápido
    │   ├── InventarioPage.vue          #   CRUD productos + filtros
    │   ├── UsuariosPage.vue            #   CRUD usuarios + filtros
    │   └── ClientesPage.vue            #   CRUD clientes + filtros
    ├── router/                         # Vue Router
    │   ├── index.ts                    #   Creación + guard de navegación
    │   └── routes.ts                   #   Definición de rutas
    ├── stores/                         # Pinia stores
    │   ├── index.ts                    #   createPinia()
    │   ├── auth-store.ts               #   Auth: login/logout, persistencia LocalStorage
    │   ├── producto-store.ts           #   CRUD productos paginado
    │   ├── usuario-store.ts            #   CRUD usuarios paginado
    │   └── cliente-store.ts            #   CRUD clientes paginado
    └── utils/                          # Utilidades
        └── pdf.ts                      #   Generación de PDF con jsPDF
```

---

## 3. RUTAS Y NAVEGACIÓN

### Definición de rutas (`src/router/routes.ts`)

| Ruta | Componente | Layout | Protegida | Descripción |
|------|-----------|--------|-----------|-------------|
| `/login` | `LoginPage.vue` | — | No | Login público |
| `/` | `MainLayout.vue` | Sí | Sí | Layout con header/drawer |
| `/dashboard` | `DashboardPage.vue` | MainLayout | Sí | Página principal post-login |
| `/inventario` | `InventarioPage.vue` | MainLayout | Sí | CRUD productos |
| `/usuarios` | `UsuariosPage.vue` | MainLayout | Sí | CRUD usuarios |
| `/clientes` | `ClientesPage.vue` | MainLayout | Sí | CRUD clientes |

La ruta raíz `/` redirige a `/dashboard`.

### Guard de navegación (`src/router/index.ts`)

```
beforeEach:
  ¿to.path !== '/login' Y no hay token?  → redirigir a /login
  ¿to.path === '/login' Y hay token?     → redirigir a /dashboard
  en otro caso                           → next()
```

Usa `createWebHashHistory()` (rutas con `#/`).

---

## 4. BOOT — Axios (`src/boot/axios.ts`)

### Configuración

```
baseURL: '/api'        (relativo; Quasar devServer proxy resuelve a localhost:8090)
headers: Content-Type: application/json
```

### Interceptors

**Request:**
```
Antes de cada petición:
  Si existe token en LocalStorage → agregar header Authorization: Bearer <token>
```

**Response:**
```
En error de respuesta:
  Si status === 401 → limpiar token y usuario de LocalStorage → redirigir a /#/login
  En cualquier error → rechazar Promise (propaga al caller)
```

### Exportaciones

- `api` — instancia de Axios configurada (usada por los módulos API)
- `$axios` — Axios global en propiedades de Vue
- `$api` — instancia preconfigurada global

---

## 5. STORES (Pinia)

### 5.1 `auth-store.ts`

**State** (inicializado desde LocalStorage):
```
token: string | null
email: string | null
nombre: string | null
apellido: string | null
rol: string | null
```

**Getters:**
- `isAuthenticated` → `!!state.token`
- `nombreCompleto` → `"${nombre} ${apellido}"` (trim)

**Actions:**
- `login(credentials: LoginRequest)` → llama a `auth.api.login()`, persiste token + email + nombre + apellido + rol en LocalStorage
- `logout()` → limpia state y elimina todas las claves de LocalStorage

**Persistencia:** LocalStorage (claves: `token`, `email`, `nombre`, `apellido`, `rol`)

### 5.2 `producto-store.ts`

**State:**
```
productos: ProductoResponse[]
loading: boolean
page: number        (0-based)
size: number        (default 10)
totalElements: number
totalPages: number
```

**Actions (todas recargan la lista actual tras la operación):**
- `listar(params?)` → GET /productos con paginación
- `crear(data)` → POST /productos → recarga
- `actualizar(id, data)` → PUT /productos/{id} → recarga
- `eliminar(id)` → DELETE /productos/{id} → recarga
- `toggleActivo(id)` → PATCH /productos/{id}/toggle-activo → recarga

### 5.3 `usuario-store.ts`

Mismo patrón que `producto-store` pero sobre `/usuarios`.

**Actions:** `listar`, `crear`, `actualizar`, `eliminar`, `toggleActivo`

### 5.4 `cliente-store.ts`

Mismo patrón que `producto-store` pero sobre `/clientes`.

**Actions:** `listar`, `crear`, `actualizar`, `eliminar`, `toggleActivo`

---

## 6. API LAYER

Cada módulo en `src/api/` exporta funciones que llaman a la instancia `api` de Axios.

### 6.1 `auth.api.ts`

| Función | Método | Endpoint |
|---------|--------|----------|
| `login(data)` | POST | `/auth/login` |

**Tipos exportados:** `LoginRequest` (email, password), `LoginResponse` (token, email, nombre, apellido, rol)

### 6.2 `producto.api.ts`

| Función | Método | Endpoint |
|---------|--------|----------|
| `listarProductos(params?)` | GET | `/productos` |
| `obtenerProducto(id)` | GET | `/productos/{id}` |
| `buscarPorCodigo(codigo)` | GET | `/productos/codigo/{codigo}` |
| `crearProducto(data)` | POST | `/productos` |
| `actualizarProducto(id, data)` | PUT | `/productos/{id}` |
| `eliminarProducto(id)` | DELETE | `/productos/{id}` |
| `toggleActivo(id)` | PATCH | `/productos/{id}/toggle-activo` |

**Tipos:** `ProductoRequest`, `ProductoResponse`, `PageResponse<T>`, `ListarParams`

### 6.3 `usuario.api.ts`

| Función | Método | Endpoint |
|---------|--------|----------|
| `listarUsuarios(params?)` | GET | `/usuarios` |
| `obtenerUsuario(id)` | GET | `/usuarios/{id}` |
| `buscarPorEmail(email)` | GET | `/usuarios/email/{email}` |
| `crearUsuario(data)` | POST | `/usuarios` |
| `actualizarUsuario(id, data)` | PUT | `/usuarios/{id}` |
| `eliminarUsuario(id)` | DELETE | `/usuarios/{id}` |
| `toggleActivo(id)` | PATCH | `/usuarios/{id}/toggle-activo` |

**Tipos adicionales:** `RolUsuario` (`ROLE_ADMIN`, `ROLE_USER`, `ROLE_VENDEDOR`)

### 6.4 `cliente.api.ts`

| Función | Método | Endpoint |
|---------|--------|----------|
| `listarClientes(params?)` | GET | `/clientes` |
| `obtenerCliente(id)` | GET | `/clientes/{id}` |
| `buscarPorEmail(email)` | GET | `/clientes/email/{email}` |
| `crearCliente(data)` | POST | `/clientes` |
| `actualizarCliente(id, data)` | PUT | `/clientes/{id}` |
| `eliminarCliente(id)` | DELETE | `/clientes/{id}` |
| `toggleActivo(id)` | PATCH | `/clientes/{id}/toggle-activo` |

### Tipos compartidos

```
PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number       // página 0-based
}

ListarParams {
  page?: number
  size?: number        // default 10
  sortBy?: string
  sortDir?: 'asc' | 'desc'
}
```

---

## 7. PÁGINAS (Vue Components)

### 7.1 `LoginPage.vue`

**Layout:** Full-screen centrado vertical/horizontal, fondo `bg-grey-2`.

**Componentes Quasar:** `QCard`, `QForm`, `QInput`, `QBtn`, `QIcon`

**Flujo:**
1. Usuario ingresa email y password
2. Validaciones: campos requeridos (regla `required`)
3. Submit → `authStore.login({ email, password })`
4. Éxito → `router.push('/dashboard')`
5. Error → `Notify.create({ type: 'negative', message })` con mensaje del backend

**UI:**
- Icono `point_of_sale` (64px, color primary)
- Input email con icono `email`
- Input password con icono `lock` + toggle visibility (`visibility`/`visibility_off`)
- Botón "Iniciar sesión" full-width con loading state

### 7.2 `DashboardPage.vue`

**Layout:** Padding `q-pa-md`.

**Contenido:** Título "Dashboard" + 2 tarjetas clickeables con `v-ripple` y `to`:
- **Inventario:** Icono `inventory_2`, título, descripción → navega a `/inventario`
- **Usuarios:** Icono `people`, título, descripción → navega a `/usuarios`

### 7.3 `MainLayout.vue`

**Estructura:** `QLayout` con `QHeader` + `QDrawer` + `QPageContainer`.

**Header (QHeader):**
- Botón hamburguesa toggle drawer
- Título "Pventafront"
- Botón logout: muestra `authStore.nombreCompleto` + icono `logout`

**Drawer lateral (QDrawer):**
- Ítems de navegación con iconos:
  - Dashboard (`dashboard`)
  - Inventario (`inventory_2`)
  - Clientes (`people`)
  - Usuarios (`manage_accounts`)
- Separador
- Toggle "Tema oscuro" (`QToggle`) con persistencia en LocalStorage (clave `darkMode`):
  - `onMounted`: Lee `LocalStorage.getItem('darkMode')` y aplica `$q.dark.set(true)`
  - `toggleDark(val)`: Aplica `$q.dark.set(val)` y persiste en LocalStorage

**Logout:** `authStore.logout()` → `router.push('/login')`

### 7.4 `InventarioPage.vue`

**Header:** Título "Inventario" + botones "Imprimir" (secondary, icon `print`) + "Nuevo producto" (primary, icon `add`)

**Filtros (QExpansionItem):**
- Texto: busca en código, nombre, descripción (debounce 300ms)
- Rango precios: compra min/max, venta min/max (type number)
- Rango stock: min/max
- Estado: select `['Activo', 'Inactivo']`
- Botón "Limpiar filtros"

**Tabla (QTable):** Paginación server-side (`@request`), columnas sortables.

| Columna | Alias | Slot | Formato |
|---------|-------|------|---------|
| Código | `codigo` | — | Texto plano |
| Nombre | `nombre` | — | Texto plano |
| Descripción | `descripcion` | Sí | Truncado a 50 chars + botón expandir |
| Compra | `precioCompra` | Sí | `$ 0.00` |
| Venta | `precioVenta` | Sí | `$ 0.00` |
| Stock | `existencia` | Sí | Centrado |
| Estado | `activo` | Sí | Chip verde/rojo "Activo"/"Inactivo" |
| Acciones | `acciones` | Sí | Editar, Toggle, Eliminar |

**Diálogo crear/editar (QDialog):**
- Campos: código, nombre, descripción (textarea), precioCompra, precioVenta, existencia
- Validaciones: required en código, nombre, precioCompra, precioVenta; min >= 0 en precios
- Botones: Cancelar (v-close-popup), Guardar (submit)

**Operaciones:**
- Editar → precarga datos existentes en formulario
- Toggle activo → PATCH `/productos/{id}/toggle-activo`, Notify success/error
- Eliminar → DELETE `/productos/{id}`, Notify success/error

**PDF:** Genera con filtros aplicados (ver sección 8).

### 7.5 `UsuariosPage.vue`

**Header:** Título "Usuarios" + botones "Imprimir" + "Nuevo usuario"

**Filtros:**
- Texto: busca en nombre, apellido, email
- Rol: select `['ROLE_ADMIN', 'ROLE_USER', 'ROLE_VENDEDOR']`
- Estado: `['Activo', 'Inactivo']`

**Tabla (QTable):**

| Columna | Slot | Detalle |
|---------|------|---------|
| Nombre completo | Sí | `{{nombre}} {{apellido}}` |
| Email | — | Texto plano |
| Rol | Sí | Chip colorido: Admin=purple, Usuario=blue, Vendedor=orange |
| Teléfono | — | Texto plano |
| Estado | Sí | Chip verde/rojo |
| Acciones | Sí | Editar, Toggle, Eliminar |

**Diálogo crear/editar:**
- Campos: nombre, apellido, email, password (con toggle visibility), rol (select), teléfono
- **En edición:** password no es requerido; badge "Dejar vacío para no cambiar"
- Validaciones: required (excepto password en edición), email regex
- En el guardado: si editando y password vacío, se excluye del payload (no se envía)

### 7.6 `ClientesPage.vue`

**Header:** Título "Clientes" + botones "Imprimir" + "Nuevo cliente"

**Filtros:**
- Texto: busca en nombre, apellido, email, documento
- Estado: `['Activo', 'Inactivo']`

**Tabla (QTable):**

| Columna | Slot | Detalle |
|---------|------|---------|
| Nombre completo | Sí | `{{nombre}} {{apellido}}` |
| Email | — | Texto plano |
| Teléfono | — | Texto plano |
| Dirección | — | Texto plano |
| Documento | — | Texto plano (DNI/RUC) |
| Estado | Sí | Chip verde/rojo |
| Acciones | Sí | Editar, Toggle, Eliminar |

**Diálogo crear/editar:**
- Campos: nombre, apellido, email, teléfono, dirección, documento
- Validaciones: required en nombre, apellido, email

---

## 8. UTILIDAD PDF (`src/utils/pdf.ts`)

### Función `generarPdf`

```
generarPdf(
  titulo: string,
  columnas: { label: string; dataKey: string }[],
  datos: Record<string, unknown>[],
  nombreArchivo: string,
  filtros?: Record<string, string>
): void
```

**Comportamiento:**
1. Crea instancia `jsPDF` en orientación landscape, A4
2. Renderiza título (font 18) en posición (14, 22)
3. Renderiza fecha de generación (font 10) en (14, 30)
4. Si hay filtros activos, los muestra como línea de texto "Filtros: clave: valor | clave2: valor2" (font 9)
5. Genera tabla con jsPDF-autotable:
   - `startY` dinámico (36 sin filtros, 42 con filtros)
   - `styles.fontSize: 7`
   - `headStyles.fillColor: [25, 118, 210]` (azul primary)
6. Abre PDF en nueva pestaña vía `window.open(doc.output('bloburl'))`

**Formateo de datos:**
| dataKey | Formato |
|---------|---------|
| `precioCompra`, `precioVenta` | `$ {valor.toFixed(2)}` |
| `activo` | "Activo" si true, "Inactivo" si false |
| `rol` | Mapa: ROLE_ADMIN→"Admin", ROLE_USER→"Usuario", ROLE_VENDEDOR→"Vendedor" |
| `nombreCompleto` | `{nombre} {apellido}` |
| Otros | `String(val ?? '')` |

---

## 9. CSS (`src/css/app.scss`)

```scss
body {
  margin: 0;
  padding: 0;
}
```

Único estilo global: reset básico de body. El resto del diseño visual es provisto por Quasar (componentes, utilidades, temas).

---

## 10. CONFIGURACIÓN DE DESARROLLO

### `quasar.config.js`

| Sección | Valor |
|---------|-------|
| `boot` | `['axios']` |
| `css` | `['app.scss']` |
| `extras` | `['material-icons']` |
| `build.distDir` | `'dist'` |
| `devServer.port` | `9000` |
| `devServer.host` | `'0.0.0.0'` |
| `devServer.open` | `false` |
| `devServer.proxy` | `/api` → `http://localhost:8090` (changeOrigin: true) |
| `framework.iconSet` | `'material-icons'` |
| `framework.lang` | `'es'` |
| `framework.plugins` | `['Dialog', 'Notify', 'Loading', 'LocalStorage']` |
| `vite.server.allowedHosts` | `['miguel-desktop.local']` |

### `tsconfig.json`

| Opción | Valor |
|--------|-------|
| `target` | `ES2022` |
| `module` | `ES2022` |
| `moduleResolution` | `bundler` |
| `strict` | `true` |
| `lib` | `['ES2022', 'DOM', 'DOM.Iterable']` |
| `paths` | `@/*` → `./src/*` |
| `baseUrl` | `.` |
| `include` | `env.d.ts`, `src/**/*.ts`, `src/**/*.vue` |
| `exclude` | `node_modules`, `dist` |

---

## 11. FLUJO DE DATOS — DIAGRAMA DE INTERACCIÓN

```
Usuario
  │
  ▼
Página (Vue Component)
  │  llama a acciones del Store
  ▼
Store (Pinia)
  │  llama a funciones API
  ▼
API Layer (Axios calls)
  │  interceptors agregan Bearer token
  ▼
Axios Instance (/api baseURL)
  │
  ├── [DEV] Quasar Proxy → http://localhost:8090/api/*
  │
  ▼
Backend Spring Boot (pventabase)
  │  JWT Authentication Filter
  │  ↓
  │  Controller → Service → Repository → PostgreSQL
  │
  ▼
Respuesta JSON (PageResponse<T> o T)
  │
  ▼
Store actualiza state reactivo
  │
  ▼
Vue re-renderiza tabla/datos
```

---

## 12. MANEJO DE ERRORES — FLUJO COMPLETO

```
Error HTTP (cualquier status ≥ 400)
  │
  ├── 401 Unauthorized → Interceptor response
  │     └── Limpia LocalStorage → window.location = /#/login
  │
  ├── 400/404/409/etc → Error propagado al Store → a la Página
  │     └── Notify.create({ type: 'negative', message })
  │           message = err.response?.data?.message || 'Mensaje genérico'
  │
  └── Error de red (sin respuesta)
        └── Notify.create({ type: 'negative', message: 'Error de conexión' })
```

---

## 13. TEMA OSCURO — IMPLEMENTACIÓN

1. **Persistencia:** Clave `darkMode` en LocalStorage (boolean)
2. **Inicialización:** `MainLayout.vue` en `onMounted` lee y aplica `$q.dark.set(true)` si está activo
3. **Toggle:** `QToggle` en el drawer → llama `toggleDark(val)` que:
   - Aplica `$q.dark.set(val)`
   - Persiste `LocalStorage.set('darkMode', val)`
4. Quasar maneja automáticamente los estilos oscuros en todos los componentes

---

## 14. EXPANSIÓN DE DESCRIPCIÓN (Inventario)

Mecanismo para descripciones largas en la tabla de productos:

1. Cada fila tiene una propiedad reactiva local `_expandDesc: boolean` (false por defecto)
2. La celda de descripción renderiza:
   - Si `!_expandDesc` y length > 50 → trunca a 50 chars + "..."
   - Si `_expandDesc` → muestra texto completo
   - Botón `expand_more` visible solo si length > 50
3. `filteredRows` computed mapea los productos agregando `_expandDesc: false` a cada uno

---

## 15. CONSIDERACIONES DE SEGURIDAD

- El token JWT se almacena en `LocalStorage` (vulnerable a XSS, pero aceptable para SPA sin SSR)
- El interceptor 401 protege contra tokens expirados redirigiendo al login
- No se envían credenciales en URLs (solo en body POST)
- El proxy de desarrollo evita exponer CORS en producción (el backend ya acepta cualquier origin con `setAllowedOriginPatterns("*")`)
- Password opcional en edición de usuarios evita re-envío innecesario
