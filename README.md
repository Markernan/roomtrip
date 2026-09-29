# RoomTrip

Sistema de gestión de reservas de alojamiento en hoteles vía aplicación móvil.

Proyecto del curso **1TEL05 — Servicios y Aplicaciones para IoT**
Ingeniería de las Telecomunicaciones, PUCP — Semestre 2026-2.

---

## Descripción

RoomTrip está compuesto por **dos aplicaciones con dos persistencias separadas**:

1. **App móvil Android nativa (Java)** — usada por los cuatro roles del sistema.
2. **Servicio web de gestión de taxistas** — expone una API REST que consume la app móvil.

> **Restricción dura del enunciado:** la app móvil **nunca** accede directamente a la
> base de datos del sistema de taxistas. Toda información de taxistas pasa por la API REST.

## Estado actual del proyecto

> **La app móvil funciona hoy con datos de prueba** (`data/MockData.java`). Todavía **no**
> usa Firebase, Retrofit, almacenamiento local ni notificaciones: eso corresponde a los
> Labs 5, 6 y 7. La arquitectura de abajo describe el **diseño objetivo**.

| Área | Estado |
|---|---|
| Pantallas de los 4 roles (cliente, admin de hotel, taxista, superadmin) | Implementadas con navegación y listas `RecyclerView` sobre datos de prueba |
| Almacenamiento local y notificaciones (Lab 5) | No iniciado |
| Firebase Authentication y base de datos (Lab 6) | No iniciado |
| Consumo de la API de taxistas con Retrofit (Lab 7) | No iniciado |
| Servicio web de taxistas | Implementado en la rama [`web-taxis`](https://github.com/Markernan/roomtrip/tree/web-taxis) (ver [`taxi-service/`](taxi-service/README.md)) |
| Integración continua | GitHub Actions compila, corre lint y las pruebas unitarias en cada PR |

## Restricciones tecnológicas

| Restricción | Valor |
|---|---|
| Plataforma móvil | Android nativo en **Java** (no Kotlin, no Flutter, no React Native) |
| Versión mínima | **Android 14 — API level 34** |
| Persistencia app principal | Base de datos **NoSQL** (Firestore, a integrar en el Lab 6) |
| Contraseñas | Nunca almacenadas en texto plano |

## Arquitectura (diseño objetivo)

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
    └── taxi-service         → servicio web independiente + su propia BD (MongoDB Atlas)
```

Decisiones de diseño ya tomadas:

- **Una sola app móvil, un solo login.** Tras autenticar se lee el campo `rol` del
  usuario y se navega a la pantalla del rol correspondiente. No son cuatro apps.
- **Firestore para el dominio, Realtime Database para lo que se escribe muy seguido**
  (ubicación del taxista durante un servicio activo, mensajes de chat).
- **Retrofit** con una única capa `TaxiRepository` que encapsula todas las llamadas a
  la API de taxistas. Ahí vive el manejo de "API no disponible": se informa al usuario
  sin romper el resto del sistema de reservas.
- **Reglas de seguridad de Firestore por rol.** Un cliente solo puede leer sus propias
  reservas, chats, pagos y servicios de taxi.
- **El servicio de taxistas es Spring Boot con MongoDB Atlas** (gateway con circuit
  breaker, dos microservicios de dominio y un portal web). El detalle está en
  [`taxi-service/README.md`](taxi-service/README.md) y en [`docs/arquitectura.md`](docs/arquitectura.md).

## Estructura del repositorio

```
roomtrip/
├── app/                  App móvil nativa Java (módulo Android)
├── taxi-service/         Documentación del servicio de taxistas (el código está en la rama web-taxis)
├── docs/                 Arquitectura, manuales, OPEX, requerimientos, declaración de IA
├── .github/              Plantilla de pull request y flujo de CI (Android CI)
├── PROJECT_CONTEXT.md    Contexto técnico para desarrolladores y asistentes de IA
└── README.md
```

## Roles

| Rol | Responsabilidad principal |
|---|---|
| **Superadmin** | Gestiona usuarios, registra hoteles y asigna administradores, aprueba taxistas, reportes y visor de logs |
| **Administrador de hotel** | Registra hotel, habitaciones y servicios; procesa cobros y checkout; responde chats; reportes de ingresos |
| **Taxista** | Se autoregistra en la web, es habilitado por el Superadmin; acepta pedidos y reporta ubicación y estados |
| **Cliente** | Se autoregistra; consulta y reserva habitaciones, paga, chatea, hace checkout, valora y pide taxi al aeropuerto |

### Estados

Los estados son `enum` en `app/src/main/java/com/example/roomtrip/data/model/`. El nombre de
cada valor es el que se guarda en la base de datos y el que viaja por la API; el texto que ve
el usuario sale de `getEtiqueta()`.

| Enum | Valores | Regla del ERS |
|---|---|---|
| `EstadoServicioTaxi` | `SOLICITADO → ASIGNADO → EN_CAMINO → EN_TRASLADO → FINALIZADO` | RN-010: no se omiten estados. `FINALIZADO` solo se registra escaneando el código QR del cliente (RN-011) |
| `EstadoReserva` | `PENDIENTE`, `CONFIRMADA`, `EN_CURSO`, `FINALIZADA`, `CANCELADA` | RF-RES-010. "Reserva activa" = `CONFIRMADA` o `EN_CURSO` |
| `EstadoCuenta` | `ACTIVO`, `INACTIVO` | RF-USR-007 (hoteles) |
| `EstadoCheckout` | `PENDIENTE`, `PROCESADO` | El ERS no los define; formalizan lo que ya muestra la lista de checkouts del admin |
| `EstadoHabitacion` | `DISPONIBLE`, `OCUPADA`, `MANTENIMIENTO` | El ERS no los define; formalizan lo que ya muestra el Inventario. Falta decidir con el equipo cómo modelar la disponibilidad (RF-HOT-006 y RF-HOT-010) |

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

| Herramienta | Versión | Nota |
|---|---|---|
| Android Studio | **Ladybug (2024.2.1) o superior** | Es la versión que corresponde al Android Gradle Plugin 8.7.3 del proyecto ([tabla oficial](https://developer.android.com/build/releases/about-agp)) |
| JDK | El que trae Android Studio (17 o superior) | No hace falta instalar otro. El proyecto no fija un JDK |
| Dispositivo o emulador | **Android 14 (API 34) o superior** | `minSdk` es 34 |
| Docker Desktop, JDK 21 y Maven 3.9 | Solo para el servicio de taxistas | Ver [`taxi-service/README.md`](taxi-service/README.md) |
| Cuenta de Firebase | Desde el Lab 6 | Todavía no se necesita |

### App móvil

1. Clonar el repositorio y abrir la carpeta raíz (`roomtrip/`) en Android Studio.
2. Esperar a que Gradle sincronice. Android Studio genera `local.properties` (no se versiona).
3. Ejecutar sobre un dispositivo o emulador con API 34 o superior. La pantalla de inicio
   pide elegir un rol; no hay autenticación real todavía.

Por línea de comandos (desde la raíz):

```bash
./gradlew assembleDebug        # compilar
./gradlew testDebugUnitTest    # pruebas unitarias
./gradlew lintDebug            # análisis estático
```

> Si `lintDebug` falla con un error interno de `AndroidLintWorkAction`, ejecutarlo con
> JDK 17 o 21 (el CI usa 21): en nuestras pruebas falló con el JDK 25 que traen algunas
> versiones recientes de Android Studio. Compilar con `assembleDebug` sí funciona.

> **Al hacer `git pull` por primera vez tras la limpieza de `local.properties`:** si git avisa
> que `local.properties` se sobrescribiría, guarda tu copia, descarta el cambio y vuélvela a
> poner después del pull (el archivo ya está en `.gitignore`).

### Servicio de taxistas

Ver [`taxi-service/README.md`](taxi-service/README.md).

## Convenciones de trabajo

- Rama `main` protegida. Se entra **solo por pull request**, con el CI en verde.
- Ramas de trabajo: `feat/<rol>-<funcionalidad>` — por ejemplo `feat/superadmin-gestion-usuarios`.
- Commits en español con prefijo: `feat:`, `fix:`, `docs:`, `refactor:`, `chore:`.
- Un tag por hito entregado: `v0.2-lab2`, `v0.3-lab3`, etc.
- Nombres de colecciones, campos y variables de dominio en español, consistentes con el
  enunciado (`reservas`, `habitaciones`, `serviciosTaxi`, `valoraciones`, `logs`).
- Los datos de prueba viven en `data/MockData.java`, no dentro de las pantallas.

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
