# Agents

## Commands

| Action | Command |
|--------|---------|
| Dev server | `npm start` (port 4200, proxies `/api` → `http://localhost:8082`) |
| Build | `npm run build` → `dist/laundry-pos-frontend` |
| Test | `npm test` (Karma + Jasmine, Chrome) |
| Lint | `npm run lint` (not configured — `ng lint` will fail) |
| Docker | `docker build -t laundry-frontend . && docker run -p 80:80 laundry-frontend` |

## Architecture

- **Angular 17 standalone** — no NgModules. `bootstrapApplication` in `src/main.ts`.
- **DI**: `inject()` only — no constructor injection anywhere.
- **Routing**: 10 lazy-loaded feature modules (`auth`, `dashboard`, `pos`, `clients`, `inventory`, `servicios`, `suppliers`, `reports`, `expenses`, `cash-closure`). Non-auth routes guarded with `authGuard` — redirects to `/auth`.
- **Auth**: JWT in `localStorage` (`token`, `username`, `role`). Functional `authInterceptor` adds `Authorization: Bearer`. Functional `authGuard` checks `AuthService.isLoggedIn()`.
- **API paths** hardcoded as `/api/...` in `ApiService` — `environment.apiUrl` is **not** used at runtime.
- **Components** use inline templates/styles — no separate `.html`/`.css` files.
- **UI**: PrimeNG 17 + PrimeFlex + PrimeIcons. Theme: `lara-dark-blue`.
- **Charting**: chart.js (used in reports).

## Gotchas

- `POSComponent.createOrder()` hardcodes `userId: 1` (`src/app/modules/pos/pos.component.ts:276`).
- No `.gitignore` — `git add` could commit `node_modules/`.
- Proxy target is `localhost:8082` (in `proxy.conf.json`), not `8080`.
- Docker nginx proxies `/api` → `http://backend:8085/api` (Docker network), not `localhost`.
- No `docker-compose.yml` at root — README mentions one but it doesn't exist.

<!-- CODEGRAPH_START -->
## CodeGraph

In repositories indexed by CodeGraph (a `.codegraph/` directory exists at the repo root), reach for it BEFORE grep/find or reading files when you need to understand or locate code:

- **MCP tools** (when available): `codegraph_explore` answers most code questions in one call — the relevant symbols' verbatim source plus the call paths between them. `codegraph_node` returns one symbol's source + callers, or reads a whole file with line numbers. If the tools are listed but deferred, load them by name via tool search.
- **Shell** (always works): `codegraph explore "<symbol names or question>"` and `codegraph node <symbol-or-file>` print the same output.

If there is no `.codegraph/` directory, skip CodeGraph entirely — indexing is the user's decision.
<!-- CODEGRAPH_END -->
