# AGENTS.md — pventafront

Frontend SPA de Punto de Venta. Vue 3 + Quasar 2, TypeScript, Pinia, Axios, jsPDF.

## Comandos

| Comando | Descripción |
|---------|-------------|
| `quasar dev` | Dev server en `http://0.0.0.0:9000` |
| `quasar build` | Build a `dist/` |

No hay lint, typecheck ni tests configurados.

## Arquitectura

- **Hash routing** (`createWebHashHistory`), rutas con `#/`
- **Estilo:** Options API con `setup()` (NO `<script setup>`, NO Composition API `<script>`)
- **Store pattern:** `defineStore` Options API (`state`/`getters`/`actions`)
- **API layer:** Cada módulo en `src/api/*.api.ts`, llama a instancia Axios con `baseURL: '/api'`
- **Tipos compartidos:** `src/api/types.ts` (`LoginRequest`, `LoginResponse`, `PageResponse<T>`, `ListarParams`)
- **Tipos específicos:** Definidos en cada `src/api/*.api.ts`

## Estructura por módulo

Cada entidad (productos, usuarios, clientes) sigue:

```
src/api/{entidad}.api.ts     → funciones HTTP + interfaces Request/Response
src/stores/{entidad}-store.ts → Pinia store (listar/crear/actualizar/eliminar/toggleActivo)
src/pages/{entidad}Page.vue   → Vista CRUD con QTable + QDialog
```

Los stores **siempre recargan la lista** tras cada mutación (crear/actualizar/eliminar/toggle).

## Convenios importantes

- **Autenticación:** JWT en `LocalStorage` (claves: `token`, `email`, `nombre`, `apellido`, `rol`). Interceptor Axios agrega `Authorization: Bearer` y redirige a `/login` en 401.
- **Paginación:** API usa page 0-based, QTable usa page 1-based. Convertir con `page - 1` al llamar al store.
- **Proxy dev:** `quasar.config.js` proxy `/api` → `http://localhost:8090`. No tocar CORS en desarrollo.
- **Manejo de errores:** `Notify.create({ type: 'negative', message: err.response?.data?.message || 'Mensaje genérico' })`
- **Idioma:** Quasar en español (`lang: 'es'`), `material-icons`
- **Tema oscuro:** Persiste en LocalStorage clave `darkMode`
- **Password en edición de usuarios:** Si vacío, se excluye del payload (no se envía al backend)

## PDF (`src/utils/pdf.ts`)

`generarPdf(titulo, columnas, datos, nombreArchivo, filtros?)` — landscape A4, abre blob en nueva pestaña.

## Perfil
Eres un experto en desarrollo de software y buscas siempre las mejores soluciones, dominas Java, Spring boot, Angular, React, Bases de datos postgresql, Oracle, Mysql, redis... y engeneral de todo lo que requiere la industria. Siempre respondes de forma simple y entendible, no debes extenderte demasiado en tus respuestas solo cuando te solicite explicacion debes extenderte, limita siempre tus respuestas para que sean cortas pero entendibles. Siempre RESPONDE EN ESPAÑOL y Siempre debes terminar tus respuestas con el siguiente mensaje: "----------RESPUESTA---------"

<!-- CODEGRAPH_START -->
## CodeGraph

In repositories indexed by CodeGraph (a `.codegraph/` directory exists at the repo root), reach for it BEFORE grep/find or reading files when you need to understand or locate code:

- **MCP tools** (when available): `codegraph_explore` answers most code questions in one call — the relevant symbols' verbatim source plus the call paths between them. `codegraph_node` returns one symbol's source + callers, or reads a whole file with line numbers. If the tools are listed but deferred, load them by name via tool search.
- **Shell** (always works): `codegraph explore "<symbol names or question>"` and `codegraph node <symbol-or-file>` print the same output.

If there is no `.codegraph/` directory, skip CodeGraph entirely — indexing is the user's decision.
<!-- CODEGRAPH_END -->
