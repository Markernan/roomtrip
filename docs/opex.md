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
| Hosting `taxi-service` (4 servicios) | Google Cloud Run | Free tier | 0.00 | Estimación del escenario del curso, ver nota 1 |
| MongoDB | MongoDB Atlas | M0 (gratis) | 0.00 | Menos de 50 MB, ver nota 1 |
| Fotos de taxistas | Cloudinary | Free | 0.00 | Unas 60 imágenes, ver nota 1 |
| Dominio | | | | |
| **Total** | | | | Por completar: faltan los servicios de Firebase |

## 3. Costo anual proyectado

Por completar una vez cerrada la tabla anterior.

## 4. Observaciones

1. Las filas de `taxi-service` provienen de `docs/DESPLIEGUE.md` de la rama `web-taxis` (sección
   OPEX), que estima USD 0.00 para el escenario del curso (5 desarrolladores, unas 30 solicitudes de
   taxista) y entre USD 21 y USD 168 al mes para un escenario de producción (500 taxistas activos).
   Son estimaciones del equipo: hay que validarlas con los precios vigentes de cada proveedor antes
   de la entrega.

- La ubicación del taxista se actualiza periódicamente durante un servicio activo: es el
  principal generador de escrituras del sistema y el rubro a vigilar en el costo. Esa es
  la razón de usar Realtime Database en lugar de Firestore para ese dato.
