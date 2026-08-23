# RoomTrip

Sistema de gestión de reservas de alojamiento en hoteles vía aplicación móvil.

Proyecto del curso **1TEL05 — Servicios y Aplicaciones para IoT**
Ingeniería de las Telecomunicaciones, PUCP — Semestre 2026-2.

---

## Descripción

RoomTrip está compuesto por **dos aplicaciones con dos persistencias separadas**:

1. **App móvil Android nativa (Java)** — usada por los cuatro roles del sistema.
2. **App web de gestión de taxistas** — expone una API REST que consume la app móvil.

> **Restricción dura del enunciado:** la app móvil **nunca** accede directamente a la
> base de datos del sistema de taxistas. Toda información de taxistas pasa por la API REST.

## Restricciones tecnológicas

| Restricción | Valor |
|---|---|
| Plataforma móvil | Android nativo en **Java** (no Kotlin, no Flutter, no React Native) |
| Versión mínima | **Android 14 — API level 34** |
| Persistencia app principal | Base de datos **NoSQL** (Firestore) |
| Contraseñas | Nunca almacenadas en texto plano |

## Arquitectura

```
App móvil Android (Java)
├── Firebase (BaaS)
│   ├── Authentication       → login único, rol resuelto tras autenticar
│   ├── Firestore            → dominio: usuarios, hoteles, habitaciones,
│   │                           servicios, reservas, cobros, valoraciones, logs
│   ├── Realtime Database    → alta frecuencia: ubicación del taxista, chat
│   ├── Storage / Cloudinary → fotos de hotel, servicios, usuarios, vehículos
│   └── Cloud Messaging      → notificaciones
└── API REST (Retrofit)
    └── taxi-service         → app web independiente + su propia BD (MongoDB)
```

Decisiones de diseño ya tomadas:

- **Una sola app móvil, un solo login.** Tras autenticar se lee el campo `rol` del
  usuario y se navega al grafo de navegación correspondiente. No son cuatro apps.
- **Firestore para el dominio, Realtime Database para lo que se escribe muy seguido**
  (ubicación del taxista durante un servicio activo, mensajes de chat).
- **Retrofit** con una única capa `TaxiRepository` que encapsula todas las llamadas a
  la API de taxistas. Ahí vive el manejo de "API no disponible": se informa al usuario
  sin romper el resto del sistema de reservas.
- **Reglas de seguridad de Firestore por rol.** Un cliente solo puede leer sus propias
  reservas, chats, pagos y servicios de taxi.

## Estructura del repositorio

```
roomtrip/
├── android-app/          App móvil nativa Java
├── taxi-service/         Web + API REST de taxistas
├── docs/                 Arquitectura, manuales, OPEX, declaración de IA
└── README.md
```

## Roles

| Rol | Responsabilidad principal |
|---|---|
| **Superadmin** | Gestiona usuarios, registra hoteles y asigna administradores, aprueba taxistas, reportes y visor de logs |
| **Administrador de hotel** | Registra hotel, habitaciones y servicios; procesa cobros y checkout; responde chats; reportes de ingresos |
| **Taxista** | Se autoregistra en la web, es habilitado por el Superadmin; acepta pedidos y reporta ubicación y estados |
| **Cliente** | Se autoregistra; consulta y reserva habitaciones, paga, chatea, hace checkout, valora y pide taxi al aeropuerto |

### Estados del servicio de taxi

```
SOLICITADO → ASIGNADO → EN_CAMINO → EN_TRASLADO → FINALIZADO
```

Al aceptar el pedido pasa automáticamente a `ASIGNADO`.
`FINALIZADO` solo se registra escaneando el código QR del cliente.

## Reglas de negocio críticas

- Una habitación **no puede** ser reservada por más de un cliente para períodos que se
  superpongan.
- Un mismo cliente **no puede** tener reservas con fechas de alojamiento superpuestas.
- El reporte de ingresos por servicios adicionales se ordena **de menor a mayor**.
- El chat cliente–hotel está disponible **solo** desde la confirmación de la reserva
  hasta el fin del checkout.
- El taxi al aeropuerto es gratuito y opcional, sujeto al monto mínimo que cada hotel
  establece.

## Puesta en marcha

### Requisitos

- Android Studio (versión con soporte para API 34)
- JDK 17
- Node.js 24+ (para `taxi-service`)
- Cuenta de Firebase con un proyecto creado

### App móvil

1. Abrir la carpeta `android-app/` en Android Studio.
2. Descargar `google-services.json` desde la consola de Firebase y colocarlo en
   `android-app/app/`. **Este archivo no está versionado.**
3. Sincronizar Gradle y ejecutar sobre un dispositivo o emulador con API 34 o superior.

### taxi-service

1. `cd taxi-service && npm install`
2. Copiar `.env.example` a `.env` y completar la cadena de conexión de MongoDB.
3. `npm run dev`

## Convenciones de trabajo

- Rama `main` protegida. Se entra **solo por pull request**.
- Ramas de trabajo: `feat/<rol>-<funcionalidad>` — por ejemplo `feat/superadmin-gestion-usuarios`.
- Commits en español con prefijo: `feat:`, `fix:`, `docs:`, `refactor:`, `chore:`.
- Un tag por hito entregado: `v0.2-lab2`, `v0.3-lab3`, etc.
- Nombres de colecciones, campos y variables de dominio en español, consistentes con el
  enunciado (`reservas`, `habitaciones`, `serviciosTaxi`, `valoraciones`, `logs`).

## Calendario de entregas

| Entregable | Contenido | Fecha |
|---|---|---|
| 2 | Lista de requerimientos y 100% de mockups | 01/09/2026 |
| 3 | Implementación de mockups: navigation, menús, elementos UI | 15/09/2026 |
| 4 | RecyclerView: listado de elementos con data estática | 29/09/2026 |
| 5 | Storage local y notificaciones | 20/10/2026 |
| 6 | Firebase Authentication y Firebase Database | 03/11/2026 |
| 7 | Presentación prefinal (85%) | 14/12/2026 |

## Uso de inteligencia artificial

El uso de herramientas de IA generativa se declara y cita en
[`docs/declaracion-ia.md`](docs/declaracion-ia.md), conforme se produce.
# roomtrip
