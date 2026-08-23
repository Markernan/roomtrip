# Arquitectura — RoomTrip

> Documento vivo. Se actualiza cuando cambia una decisión, no al final del proyecto.

## 1. Vista general

RoomTrip son **dos sistemas independientes** que se comunican exclusivamente por una
API REST:

```
┌─────────────────────────────┐        ┌──────────────────────────────┐
│   App móvil Android (Java)  │        │   taxi-service (web + API)   │
│                             │        │                              │
│   Superadmin                │  HTTPS │   Autoregistro de taxistas   │
│   Admin de hotel            │───────▶│   Habilitación (Superadmin)  │
│   Taxista                   │  REST  │   Consulta de taxistas       │
│   Cliente                   │        │                              │
└──────────────┬──────────────┘        └───────────────┬──────────────┘
               │                                       │
               ▼                                       ▼
        ┌─────────────┐                          ┌───────────┐
        │  Firebase   │                          │  MongoDB  │
        └─────────────┘                          └───────────┘
```

**Restricción dura:** la app móvil nunca accede a la base de datos de taxistas. Toda
información de taxistas pasa por la API REST.

## 2. Componentes de Firebase y por qué cada uno

| Servicio | Uso | Justificación |
|---|---|---|
| Authentication | Login único para los 4 roles | El rol se resuelve leyendo el documento del usuario después de autenticar |
| Firestore | Dominio del negocio | Consultas por campos, reglas de seguridad granulares por rol |
| Realtime Database | Ubicación del taxista y chat | Escrituras de alta frecuencia y latencia baja; Firestore encarece este patrón |
| Storage / Cloudinary | Imágenes | Fotos de hotel, servicios, usuarios y vehículos |
| Cloud Messaging | Notificaciones | Aviso de cobro al cliente, alerta de checkout al hotel |

## 3. Modelo de datos (Firestore)

> Nombres de colecciones y campos en español, consistentes con el enunciado.

| Colección | Contenido |
|---|---|
| `usuarios` | Datos comunes + `rol` + `estado` (activo/inactivo) |
| `hoteles` | Ubicación, fotos, lugares históricos cercanos, monto mínimo para taxi |
| `habitaciones` | Tipo, capacidad (adultos/niños), tamaño en m² |
| `servicios` | Nombre, descripción, precio, imágenes, si tiene costo |
| `reservas` | Cliente, habitación, fechas, estado, monto |
| `cobros` | Cobro de la reserva y cobros adicionales por daños |
| `valoraciones` | Del hotel y del taxista |
| `serviciosTaxi` | Estado del servicio, hotel, cliente, taxista, aeropuerto |
| `logs` | Eventos relevantes del sistema, consultables por el Superadmin |

### Realtime Database

```
ubicaciones/{servicioTaxiId}  → { lat, lng, actualizadoEn }
chats/{reservaId}/mensajes/   → { emisor, texto, enviadoEn }
```

## 4. Reglas de negocio que la arquitectura debe garantizar

1. **No superposición de reservas.** Una habitación no puede tener dos reservas con
   períodos que se superpongan, y un cliente no puede tener dos reservas suyas
   superpuestas. Requiere validación transaccional, no solo en el cliente.
2. **Exclusividad del pedido de taxi.** Cuando un taxista acepta un pedido, este deja de
   estar disponible para los demás. Requiere una transacción.
3. **Ventana del chat.** Disponible únicamente desde la confirmación de la reserva hasta
   el fin del checkout.
4. **Cierre por QR.** El estado `FINALIZADO` de un servicio de taxi solo se registra
   escaneando el código QR del cliente.

## 5. Seguridad

- Contraseñas gestionadas por Firebase Authentication; nunca se almacenan en texto plano
  ni se replican en Firestore.
- Reglas de seguridad de Firestore por rol: un cliente solo lee sus propias reservas,
  chats, pagos y servicios de taxi.
- Las operaciones quedan restringidas según el rol autenticado, validado del lado del
  servidor mediante las reglas, no solo ocultando UI.
- Los eventos relevantes se escriben en `logs` y solo el Superadmin puede leerlos.

## 6. Degradación ante fallo de la API de taxistas

Toda llamada a `taxi-service` pasa por `TaxiRepository`. Si la API no responde:

- Se informa al usuario con un mensaje claro.
- **El resto del sistema de reservas sigue operando con normalidad.**
- El evento se registra en `logs`.

## 7. Decisiones pendientes

- [ ] Stack de `taxi-service` (Node/Express vs. Spring Boot) — condiciona el despliegue.
- [ ] Proveedor de despliegue en la nube para `taxi-service` y MongoDB.
- [ ] Storage de imágenes: Firebase Storage vs. Cloudinary (impacta el OPEX).
- [ ] Estrategia de generación y validación del código QR.
