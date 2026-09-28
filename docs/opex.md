# Costo total de la solución (OPEX) — RoomTrip

> Plantilla. Se completa conforme se confirman los servicios contratados y sus planes.

## 1. Supuestos de dimensionamiento

| Parámetro | Valor estimado |
|---|---|
| Hoteles registrados | Por definir |
| Clientes activos / mes | Por definir |
| Reservas / mes | Por definir |
| Servicios de taxi / mes | Por definir |
| Imágenes almacenadas | Por definir |

## 2. Costos mensuales

| Servicio | Proveedor | Plan | Costo mensual (USD) | Notas |
|---|---|---|---|---|
| Authentication | Firebase | | | |
| Firestore | Firebase | | | Lecturas / escrituras / almacenamiento |
| Realtime Database | Firebase | | | Alta frecuencia: ubicación y chat |
| Storage de imágenes | Firebase / Cloudinary | | | Decisión pendiente |
| Cloud Messaging | Firebase | | | |
| Hosting `taxi-service` | Por definir | | | |
| MongoDB | Por definir | | | |
| Dominio | | | | |
| **Total** | | | | |

## 3. Costo anual proyectado

Por completar una vez cerrada la tabla anterior.

## 4. Observaciones

- La ubicación del taxista se actualiza periódicamente durante un servicio activo: es el
  principal generador de escrituras del sistema y el rubro a vigilar en el costo. Esa es
  la razón de usar Realtime Database en lugar de Firestore para ese dato.
