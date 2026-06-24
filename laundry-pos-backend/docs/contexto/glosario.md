# Glosario

## Términos del dominio

| Término | Significado |
|---|---|
| **Orden de servicio** | Transacción principal que agrupa servicios de lavandería + productos, con estado (pendiente/en proceso/listo/entregado). |
| **Servicio de lavandería** | Tipo de servicio ofrecido (ej: lavado, secado, planchado) con precio por kilo. |
| **Producto** | Artículo físico en inventario para venta adicional (ej: jabón, suavizante, bolsas). |
| **Ticket** | Comprobante impreso con número de orden, código de barras (Code 128), detalle de servicios y productos, y total. |
| **Corte de caja** | Cierre del día que calcula: efectivo inicial + ventas - gastos = efectivo esperado, vs efectivo declarado. |
| **Stock bajo** | Estado de un producto cuando `stock <= minStock`. |
| **Cliente frecuente** | Cliente registrado con datos de contacto y puntos de fidelidad (`loyaltyPoints`). |

## Entidades principales (backend)

| Entidad | Tabla | Módulo | Descripción |
|---|---|---|---|
| `User` | `users` | auth | Usuario del sistema (admin/cajero). |
| `Client` | `clients` | clients | Cliente frecuente. |
| `ServiceOrder` | `service_orders` | sales | Orden de servicio/ticket. |
| `OrderItem` | `order_items` | sales | Línea de servicio dentro de una orden. |
| `OrderProduct` | `order_products` | sales | Línea de producto dentro de una orden. |
| `ServiceType` | `service_types` | sales | Tipo de servicio lavandería. |
| `Product` | `products` | inventory | Producto en inventario. |
| `InventoryMovement` | `inventory_movements` | inventory | Movimiento de stock (entrada/salida). |
| `Supplier` | `suppliers` | suppliers | Proveedor de insumos. |
| `Expense` | `expenses` | expenses | Gasto registrado. |
| `ServiceEntity` | `services` | services | Servicio prestado (nombre + costo). |
| `Notification` | `notifications` | notifications | Registro de notificación enviada. |
| `CashClosure` | `cash_closures` | cash_closure | Corte de caja. |

## Enums

| Enum | Valores |
|---|---|
| `Role` | `ROLE_ADMIN`, `ROLE_CASHIER` |
| `OrderStatus` | `PENDIENTE`, `EN_PROCESO`, `LISTO`, `ENTREGADO` |
| `ExpenseCategory` | `ALQUILER`, `SERVICIOS`, `INSUMOS`, `MANTENIMIENTO`, `SUELDOS`, `IMPUESTOS`, `TRANSPORTE`, `OTROS` |
| `PaymentMethod` | `EFECTIVO`, `TARJETA`, `TRANSFERENCIA`, `OTRO` |
| `MovementType` | `ENTRADA`, `SALIDA` |
| `ClosureStatus` | `ABIERTO`, `CERRADO` |

## Siglas internas

| Sigla | Significado |
|---|---|
| POS | Point of Sale (Punto de Venta) |
| JWT | JSON Web Token |
| JPA | Java Persistence API |
| ZXing | Zebra Crossing (librería de barcodes) |
