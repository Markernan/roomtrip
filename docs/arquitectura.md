# Arquitectura — RoomTrip

> Documento vivo. Se actualiza cuando cambia una decisión, no al final del proyecto.
>
> Las secciones 1 a 6 describen el **diseño objetivo**. La sección 8 dice qué parte existe hoy
> en el código.

## 1. Vista general

RoomTrip son **dos sistemas independientes** que se comunican exclusivamente por una
API REST:

```
┌─────────────────────────────┐        ┌──────────────────────────────┐
│   App móvil Android (Java)  │        │   taxi-service (web + API)   │
│                             │        │                              │
│   Superadmin                │  HTTPS │   Autoregistro de taxistas   │
│   Admin de hotel            │───────▶│   Habilitación (Superadmin)  │
│   Taxista                   │  REST  │   Disponibilidad y ratings   │
│   Cliente                   │        │                              │
└──────────────┬──────────────┘        └───────────────┬──────────────┘
               │                                       │
               ▼                                       ▼
        ┌─────────────┐                        ┌───────────────┐
        │  Firebase   │                        │ MongoDB Atlas │
        └─────────────┘                        └───────────────┘
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

> Nombres de colecciones y campos en español, consistentes con el enunciado. Implementado en el
> Lab 6: el cobro vive dentro de la reserva (no hay colección `cobros`) y el chat usa Firestore
> (subcolección `mensajes`) en lugar de Realtime Database.

| Colección | Contenido |
|---|---|
| `usuarios` | Datos comunes + `rol` + `estado` (`ACTIVO` / `INACTIVO`); `estadoAprobacion` (taxista); `hotelId` (admin de hotel) |
| `invitaciones` | Id = correo en minúsculas. Hotel asignado al futuro administrador; la crea el Superadmin |
| `hoteles` | Ubicación, imagen, lugares históricos cercanos, monto mínimo para taxi, `estado`, `calificacion`, `numValoraciones` |
| `hoteles/{id}/habitaciones` | Tipo, capacidad, tamaño en m², precio por noche |
| `hoteles/{id}/servicios` | Nombre, descripción, precio (0 = gratuito) |
| `reservas` | Cliente, hotel y habitación (copiados), fechas `yyyy-MM-dd`, monto, `estado` (`EstadoReserva`), checkout (`EstadoCheckout`, valoración) y cobro (incluido el adicional por daños). Subcolección `mensajes` = chat |
| `ocupaciones` | Candados por noche (`hab_<habitación>_<noche>`, `cli_<cliente>_<noche>`) que garantizan la no superposición |
| `valoraciones` | Valoraciones **del hotel**, hechas en el checkout (id = id de la reserva) |
| `serviciosTaxi` | `estado` (`EstadoServicioTaxi`), hotel, cliente, taxista, aeropuerto. El id del documento es el contenido del QR (RN-011) |
| `logs` | Eventos relevantes del sistema, consultables por el Superadmin |

La calificación del **taxista** no se guarda aquí: la app la envía a la API
(`POST /api/calificaciones`) y la almacena `calificacion-service` en su propia base (RF-TAX-017).

Los estados se guardan por el **nombre** del `enum` (`"EN_CAMINO"`, no `"En camino"`), el mismo
que usa la API de taxistas. Ver la sección 8.

### Realtime Database

```
ubicaciones/{servicioTaxiId}  → { lat, lng, actualizadoEn }
chats/{reservaId}/mensajes/   → { emisor, texto, enviadoEn }
```

## 4. Reglas de negocio que la arquitectura debe garantizar

1. **No superposición de reservas.** Una habitación no puede tener dos reservas con
   períodos que se superpongan, y un cliente no puede tener dos reservas suyas
   superpuestas. Requiere validación transaccional, no solo en el cliente. Solo cuentan las
   reservas activas (`EstadoReserva.esActiva()`: `CONFIRMADA` o `EN_CURSO`).
2. **Exclusividad del pedido de taxi.** Cuando un taxista acepta un pedido, este deja de estar
   disponible para los demás. Requiere una transacción.
3. **Ventana del chat.** Disponible únicamente desde la confirmación de la reserva hasta
   el fin del checkout.
4. **Cierre por QR.** El estado `FINALIZADO` de un servicio de taxi solo se registra
   escaneando el código QR del cliente.
5. **Secuencia del servicio de taxi.** `SOLICITADO → ASIGNADO → EN_CAMINO → EN_TRASLADO →
   FINALIZADO`, sin omitir estados (`EstadoServicioTaxi.puedePasarA()`).

## 5. Seguridad

- Contraseñas gestionadas por Firebase Authentication; nunca se almacenan en texto plano
  ni se replican en Firestore.
- Reglas de seguridad de Firestore por rol: un cliente solo lee sus propias reservas,
  chats, pagos y servicios de taxi.
- Las operaciones quedan restringidas según el rol autenticado, validado del lado del
  servidor mediante las reglas, no solo ocultando UI.
- Los eventos relevantes se escriben en `logs` y solo el Superadmin puede leerlos.
- La API de taxistas exige un token JWT en todos sus servicios y guarda las contraseñas con BCrypt.

## 6. Degradación ante fallo de la API de taxistas

Toda llamada a `taxi-service` pasa por `TaxiRepository`. Si la API no responde:

- Se informa al usuario con un mensaje claro.
- **El resto del sistema de reservas sigue operando con normalidad.**
- El evento se registra en `logs`.

Esto se resuelve en dos lugares, y hacen falta los dos:

- **En el gateway** (`taxi-service`): circuit breaker con Resilience4j. Cuando el servicio de
  atrás no responde, responde **503** con el cuerpo `SERVICIO_NO_DISPONIBLE` (ver
  [`taxi-service/README.md`](../taxi-service/README.md)).
- **En la app**: *timeout* en el cliente HTTP y un mensaje al usuario cuando llega el 503.

## 7. Decisiones

### Ya tomadas

| Tema | Decisión | Dónde está la justificación |
|---|---|---|
| Stack de `taxi-service` | Spring Boot 3.5 con Java 21, en cuatro servicios (gateway, taxistas, calificaciones, portal web) | `docs/ARQUITECTURA.md` de la rama `web-taxis` |
| Base de datos de `taxi-service` | MongoDB Atlas M0, una base por servicio (`taxi_registry`, `taxi_ratings`) | Ídem |
| Fotos de los taxistas | Cloudinary | Ídem |
| Nube para `taxi-service` | Google Cloud Run | `docs/DESPLIEGUE.md` de la rama `web-taxis` |

La elección de Spring Boot y de la nube está registrada como asunto abierto en el ERS (Anexo D,
A2 y A3), pendiente de confirmar con el coordinador del curso.

### Pendientes

- [ ] **Storage de imágenes de la app:** Firebase Storage vs. Cloudinary (impacta el OPEX). La web
      de taxistas ya usa Cloudinary.
- [ ] **Ubicación y estado del servicio de taxi.** El ERS (RF-API-003 y RF-API-004) pide que la API
      los exponga, pero la API actual no tiene esos endpoints y el diseño de la app usa Realtime
      Database. Hay que elegir uno y alinear el ERS.
- [x] **Login del taxista en la app móvil (decidido en el Lab 6).** El taxista inicia sesión con
      **Firebase Authentication** (FirebaseUI, correo o Google), igual que el resto de roles, y su
      rol y habilitación se leen de `usuarios/{uid}` (`estadoAprobacion`: `PENDIENTE`, `APROBADO`,
      `RECHAZADO`, los mismos valores de la API). Mientras no exista la integración (Lab 7), el
      Superadmin aprueba cambiando ese campo. **Pendiente para el Lab 7:** vincular el uid con el
      id del taxista en `taxi-service` (campo `idTaxistaApi`) y tomar la aprobación de la API.
- [ ] **Generación y validación del código QR.** Decidido: el contenido del QR es el id del documento
      `serviciosTaxi`. El cliente lo genera y el taxista lo valida contra Firestore (Lab 6,
      `ServicioTaxiRepository.finalizarConQr`). Falta leerlo con la cámara en vez de simularlo (RN-011).
- [ ] **Dónde vive el código de `taxi-service`:** integrarlo a la carpeta `taxi-service/` o dejarlo
      en la rama `web-taxis`.
- [ ] **Estados de checkout y de habitación:** el ERS no los define. Hoy son los `enum` `EstadoCheckout`
      (`PENDIENTE`, `PROCESADO`) y `EstadoHabitacion` (`DISPONIBLE`, `OCUPADA`, `MANTENIMIENTO`), que solo
      formalizan lo que muestra la interfaz. Falta decidir con el equipo si la disponibilidad de una
      habitación se guarda o se calcula a partir de las reservas (RF-HOT-006 y RF-HOT-010).

## 8. Estado actual de la app móvil

Lo que existe hoy en el código, frente al diseño de arriba:

| Tema | Hoy |
|---|---|
| Datos | **Los cuatro roles** sobre Cloud Firestore, con un repositorio por colección en `data/firebase/`. Listeners en tiempo real (pedidos, reservas, chat, traslados) y transacciones para: aceptar y avanzar pedidos de taxi, crear reservas sin superposición (candados en `ocupaciones`), checkout con recálculo de la calificación del hotel y cobro del hotel. Consultas de un solo filtro: no hacen falta índices compuestos |
| Login | `login/LoginActivity`: un solo login con Firebase Authentication (FirebaseUI, correo o Google) y verificación de correo. El rol sale de `usuarios/{uid}`. Alta: cliente por autoregistro, taxista por solicitud aprobada por el Superadmin, admin de hotel por invitación, Superadmin creado en la consola |
| Seguridad | `firestore.rules` cubre todas las colecciones: nadie se asigna un rol que no le toca; el cliente solo ve sus reservas y taxis; el hotel solo los de su `hotelId`; el chat solo entre los dos participantes y con la reserva activa; los estados del taxi avanzan de uno en uno y los del checkout/cobro en su orden (también en el servidor) |
| Navegación | Cliente, taxista y superadmin usan `Activity` + `Intent`. El admin de hotel usa una sola `MainActivity` con `Fragment` y `nav_graph` (Navigation Component) |
| Listas | `RecyclerView` con su adapter en los cuatro roles |
| Vistas | ViewBinding en casi todo el admin de hotel, en el flujo del taxista, en Registrar hotel y en el checkout y taxi del cliente; el resto de cliente y superadmin sigue con `findViewById` y se migra de forma progresiva |
| Estados | `enum` (`EstadoServicioTaxi`, `EstadoReserva`, `EstadoCuenta`, `EstadoCheckout`, `EstadoHabitacion`, `EstadoAprobacion`) en `data/model/`, con pruebas unitarias, guardados por su nombre en Firestore |
| QR | El cliente genera con ZXing el QR con el id del documento de `serviciosTaxi`. El taxista lo valida contra Firestore en una transacción; la lectura con cámara todavía es simulada |
| Imágenes | Los hoteles guardan una clave (`imagen`) de una foto incluida en la app (`ImagenesHotel`). Subir fotos propias depende de la decisión de Storage |
| Notificaciones | Taxista: pedidos nuevos (listener). Cliente: aviso de cobro cuando el hotel procesa el checkout, mientras la app está abierta (listener en `AvisosCobro`). Con la app cerrada hace falta Cloud Messaging |
| Calidad | Pruebas unitarias, `lintDebug` y compilación en cada PR (GitHub Actions) |
