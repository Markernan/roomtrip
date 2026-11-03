# taxi-service

Servicio web de gestión de taxistas + API REST que consume la app móvil.
Persistencia propia en **MongoDB**, separada de Firebase.

> **Dónde está el código:** en la rama
> [`web-taxis`](https://github.com/Markernan/roomtrip/tree/web-taxis) de este repositorio. Esta
> carpeta solo contiene la documentación de cara a la app móvil. Queda por decidir si el código
> se integra aquí o se mantiene en su propia rama.

## Stack (decidido)

| Capa | Elección |
|---|---|
| Lenguaje y framework | Java 21 con Spring Boot 3.5 |
| Arquitectura | 4 servicios en contenedores: `gateway-service`, `taxista-service`, `calificacion-service` y `portal-web` |
| Gateway y resiliencia | Spring Cloud Gateway + Resilience4j (circuit breaker) |
| Base de datos | MongoDB Atlas M0: `taxi_registry` (taxistas) y `taxi_ratings` (calificaciones), una por servicio |
| Fotos | Cloudinary |
| Seguridad | Spring Security + JWT (HS256) + BCrypt; roles `SUPERADMIN`, `TAXISTA`, `APP_MOVIL` y `SERVICIO_INTERNO` |
| Frontend | Thymeleaf + Bootstrap 5 (portal del taxista y del Superadmin) |
| Despliegue previsto | Google Cloud Run |

El detalle de cada decisión y su justificación está en `docs/ARQUITECTURA.md` y
`docs/DESPLIEGUE.md` de la rama `web-taxis`.

## Responsabilidades

- Autoregistro del taxista: nombres, apellidos, tipo y número de documento, fecha de
  nacimiento, correo, teléfono, domicilio, foto, placa del auto y foto del vehículo.
- Habilitación del taxista por parte del Superadmin (`PENDIENTE` → `APROBADO` o `RECHAZADO`).
- Disponibilidad del taxista (`DISPONIBLE`, `EN_SERVICIO`, `NO_DISPONIBLE`).
- Calificaciones y valoración promedio.
- Exponer por API REST la información de taxistas que necesita la app móvil.

## Restricción dura

La app móvil **nunca** accede directamente a esta base de datos. Toda información de
taxistas pasa por la API REST del gateway.

## Contrato de la API

Todo pasa por el gateway (puerto 8080 en local). La app móvil se autentica con la credencial de
servicio `APP_MOVIL` (`POST /api/auth/login`) y manda el token en la cabecera
`Authorization: Bearer <token>`. `TaxiRepository`, en la app móvil, depende de este contrato.

| Método | Ruta | Quién puede | Requisito |
|---|---|---|---|
| POST | `/api/auth/login` | Público | RF-WTX-009 |
| POST | `/api/taxistas/registro` | Público | RF-WTX-001, 002 |
| GET | `/api/taxistas/pendientes` | SUPERADMIN | RF-WTX-004 |
| GET | `/api/taxistas` | SUPERADMIN | RF-WTX-005 |
| GET | `/api/taxistas/disponibles` | SUPERADMIN, APP_MOVIL | RF-API-001 (solo `APROBADO` + `DISPONIBLE`) |
| GET | `/api/taxistas/{id}` | SUPERADMIN, APP_MOVIL, el propio taxista | RF-WTX-005 |
| PATCH | `/api/taxistas/{id}/aprobacion` | SUPERADMIN | RF-WTX-004 |
| PATCH | `/api/taxistas/{id}/disponibilidad` | SUPERADMIN, APP_MOVIL, el propio taxista | RF-WTX-007 |
| PATCH | `/api/taxistas/{id}/perfil` | SUPERADMIN, el propio taxista | RF-WTX-005 |
| POST | `/api/calificaciones` | APP_MOVIL | RF-WTX-006, RF-TAX-017 |
| GET | `/api/calificaciones/taxista/{id}/valoracion` | SUPERADMIN, APP_MOVIL, el propio taxista | RN-013 |

Además existe `PATCH /api/taxistas/{id}/valoracion`, de uso interno entre servicios (rol
`SERVICIO_INTERNO`); la app móvil no lo usa.

### Cuando el servicio no responde

Si un servicio de atrás no responde, el gateway devuelve **503** con este cuerpo, que es lo que
la app móvil debe mostrar como aviso sin interrumpir las reservas (RF-TAX-018, RNF-FIA-001):

```json
{
  "status": 503,
  "error": "SERVICIO_NO_DISPONIBLE",
  "mensaje": "El servicio de gestion de taxistas no esta disponible en este momento. Intentalo nuevamente en unos minutos."
}
```

En la app, la otra mitad de este requisito es el *timeout* del cliente HTTP y el mensaje al usuario
(ver [`docs/arquitectura.md`](../docs/arquitectura.md), sección 6).

### Lo que el ERS pide y la API todavía no expone

El ERS (RF-API-003 y RF-API-004) pide servicios para consultar y actualizar el **estado del
servicio de taxi** y para registrar y consultar la **ubicación del taxista**. La API actual no
tiene esos endpoints; el diseño de la app prevé la ubicación en Realtime Database. Es una
decisión abierta (ver `docs/arquitectura.md`, sección 7).

## Cómo ejecutarlo en local

Requisitos: Docker Desktop, JDK 21 y Maven 3.9 o superior. Desde la raíz del código del servicio
(rama `web-taxis`):

```bash
cp .env.example .env     # completar JWT_SECRET (mínimo 32 caracteres) y las contraseñas iniciales
docker compose up --build
```

Levanta MongoDB y los cuatro servicios:

| Servicio | Puerto | Para qué |
|---|---|---|
| `gateway-service` | 8080 | API pública: la consume la app móvil |
| `taxista-service` | 8081 | Padrón de taxistas y emisión de tokens |
| `calificacion-service` | 8082 | Calificaciones y valoración promedio |
| `portal-web` | 8083 | Autoregistro del taxista y panel del Superadmin |

Compilar y probar: `mvn clean verify`.
