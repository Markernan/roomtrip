# Lista de requerimientos — RoomTrip

> **Entregable 2 — fecha límite 01/09/2026.**
> Este documento y el 100% de los mockups son el contenido de esa entrega.

## Requerimientos funcionales

### Superadmin

| ID | Requerimiento | Estado |
|---|---|---|
| RF-SA-01 | Activar y desactivar administradores de hotel, taxistas y clientes | Pendiente |
| RF-SA-02 | Registrar hoteles y asignar un administrador a cada uno | Pendiente |
| RF-SA-03 | Aprobar (habilitar) taxistas desde la app web | Pendiente |
| RF-SA-04 | Generar el reporte de reservas por hotel | Pendiente |
| RF-SA-05 | Consultar el visor de logs de eventos del sistema | Pendiente |

### Administrador de hotel

| ID | Requerimiento | Estado |
|---|---|---|
| RF-AH-01 | Registrar la ubicación del hotel y los lugares históricos cercanos | Pendiente |
| RF-AH-02 | Subir fotos del hotel (mínimo 4) | Pendiente |
| RF-AH-03 | Registrar habitaciones: tipo, capacidad (adultos y niños), tamaño en m² | Pendiente |
| RF-AH-04 | Registrar servicios: nombre, descripción, precio, imágenes y si tienen costo | Pendiente |
| RF-AH-05 | Ver el estado del taxista: asignado / en camino a recoger / llegó al destino | Pendiente |
| RF-AH-06 | Recibir la alerta de checkout y procesar el cobro de la tarjeta registrada | Pendiente |
| RF-AH-07 | Añadir cobros adicionales por daños (monto, motivo y observación) | Pendiente |
| RF-AH-08 | Reporte de ingresos por servicios adicionales, ordenado de menor a mayor | Pendiente |
| RF-AH-09 | Responder los chats de clientes con reserva activa | Pendiente |

### Taxista

| ID | Requerimiento | Estado |
|---|---|---|
| RF-TX-01 | Autoregistrarse en la app web con todos sus datos y fotos | Pendiente |
| RF-TX-02 | Acceder a la app móvil una vez habilitado | Pendiente |
| RF-TX-03 | Ver hoteles y pedidos disponibles | Pendiente |
| RF-TX-04 | Aceptar un pedido, que deja de estar disponible para los demás | Pendiente |
| RF-TX-05 | Registrar automáticamente su ubicación durante el servicio activo | Pendiente |
| RF-TX-06 | Actualizar los estados del servicio | Pendiente |
| RF-TX-07 | Registrar FINALIZADO únicamente escaneando el QR del cliente | Pendiente |

### Cliente

| ID | Requerimiento | Estado |
|---|---|---|
| RF-CL-01 | Autoregistrarse y quedar habilitado automáticamente | Pendiente |
| RF-CL-02 | Consultar hoteles, habitaciones, servicios, disponibilidad y valoraciones | Pendiente |
| RF-CL-03 | Reservar con tarjeta de crédito o débito (simulada) | Pendiente |
| RF-CL-04 | Hacer checkout y registrar valoración y observaciones obligatorias | Pendiente |
| RF-CL-05 | Recibir notificación cuando se le hace el cobro | Pendiente |
| RF-CL-06 | Ver el historial de sus reservas | Pendiente |
| RF-CL-07 | Chat privado con el hotel entre la confirmación de la reserva y el fin del checkout | Pendiente |
| RF-CL-08 | Solicitar el taxi gratuito al aeropuerto si la reserva alcanza el monto mínimo | Pendiente |
| RF-CL-09 | Ver datos, vehículo, valoración y ubicación del taxista en un mapa | Pendiente |
| RF-CL-10 | Recibir el QR del hotel y mostrarlo al llegar | Pendiente |
| RF-CL-11 | Calificar al taxista al finalizar el servicio | Pendiente |

### Reglas transversales

| ID | Requerimiento | Estado |
|---|---|---|
| RF-GN-01 | Una habitación no puede tener reservas de distintos clientes con períodos superpuestos | Pendiente |
| RF-GN-02 | Un mismo cliente no puede tener reservas con fechas de alojamiento superpuestas | Pendiente |
| RF-GN-03 | Login único: tras autenticar se resuelve el rol y se navega al flujo correspondiente | Pendiente |
| RF-GN-04 | Los mensajes de chat persisten con fecha y hora | Pendiente |

## Requerimientos no funcionales

| ID | Requerimiento |
|---|---|
| RNF-01 | Las contraseñas nunca se almacenan en texto plano |
| RNF-02 | Las operaciones están restringidas según el rol autenticado |
| RNF-03 | Un cliente solo accede a sus propios datos |
| RNF-04 | El sistema registra los eventos relevantes en logs consultables por el Superadmin |
| RNF-05 | La ubicación del taxista se actualiza periódicamente durante un servicio activo |
| RNF-06 | Si la API de taxistas está caída, la app lo informa sin afectar el resto del sistema |
| RNF-07 | La app móvil es Android nativa en Java, con versión mínima Android 14 (API 34) |
| RNF-08 | La persistencia de la app principal es una base de datos NoSQL |
| RNF-09 | La app móvil no accede directamente a la base de datos del sistema de taxistas |

## Estado de los mockups al 23/08/2026

| Sección | Cobertura | Falta |
|---|---|---|
| Cliente | ~85% | Tipo de documento y fecha de nacimiento en el registro; lugares históricos cercanos; enganchar el chat al bottom nav |
| Admin de hotel | ~80% | Invertir el orden del reporte de servicios adicionales (menor a mayor) |
| Taxista | ~5% | Todo salvo el login |
| Superadmin | 0% | Todas las pantallas |
| App web taxistas | 0% | Todas las pantallas |
