# Avance y pendientes — Lab 7 (presentación prefinal 85 %)

Estado al **17/11/2026**, rama `firebase-crud` (incluye la API de taxistas de `lab7-api-taxistas`). Este documento lista lo que ya está en el
repositorio para el Lab 7, cómo se verificó y lo que falta para cerrar el proyecto.

## 0. Estado para la presentación del 85 % (17/11/2026)

**Listo y verificado:** la rama compila (`assembleDebug`) y pasan todas las pruebas unitarias
(`testDebugUnitTest`, incluidas las 26 de `data/api`). La app se instala en el emulador y el login con Google
funciona con el `google-services.json` del equipo. Las reglas de Firestore con `taxistaApiId` ya están
publicadas.

**Listo en código, sin probar en dispositivo:** mapa del taxi y rastreo del taxista, login del taxista según la
API, disponibilidad sincronizada con la API, datos del taxista en el seguimiento del cliente y calificación del
taxista.

**Qué falta para probar el flujo completo del taxi** (es configuración, no código):

| # | Paso | Quién |
|---|---|---|
| 1 | Registrar la cuenta de taxista de prueba en el portal web (`https://portal-web-7oe536zvqa-uc.a.run.app`) **con el mismo correo** con el que entra a la app (Firebase). Si los correos no coinciden, la app dice que el taxista no está registrado | Quien hace la demo |
| 2 | Aprobar esa cuenta en el portal web | Superadmin del portal |
| 3 | Aprobarla también en la app (*Superadmin → Taxistas → Aprobar*), porque `firestore.rules` todavía exige `estadoAprobacion == APROBADO` para aceptar pedidos. Si nadie tiene la cuenta Superadmin de la app, crearla según [`manual-instalacion.md`](manual-instalacion.md), sección 5.4, o poner `estadoAprobacion = APROBADO` en `usuarios/{uid}` desde la consola | Superadmin de la app o dueño del proyecto de Firebase |
| 4 | Cada integrante que compile: `MAPS_API_KEY`, `TAXI_API_CORREO` y `TAXI_API_PASSWORD` en `local.properties` (se pasan por privado) y `google-services.json` en `app/` | Cada integrante |

**Recomendación para la demo:** si los pasos 1–3 no están listos a tiempo, mostrar el flujo de reservas, el
checkout con el pedido de taxi y los reportes (que ya funcionan sobre Firebase), y presentar el mapa y la API con
el código, las pruebas unitarias y este documento como evidencia, marcados como *pendientes de prueba en
dispositivo*.

## 1. Hecho: mapa del taxi y ubicación del taxista

Requisitos que cubre: *"en caso de pedir un taxi al aeropuerto se debe poder ver la información
del taxista y dónde se encuentra (mapa)"*, *"registrar automáticamente su ubicación durante un
servicio activo"* y el requisito de calidad *"durante un servicio activo, la ubicación del taxista
deberá actualizarse periódicamente"*.

| Parte | Qué hace | Archivos |
|---|---|---|
| Rastreo del taxista | Con el servicio en `ASIGNADO`, `EN_CAMINO` o `EN_TRASLADO`, pide la posición al *Fused Location Provider* cada 10 s y la guarda en `serviciosTaxi/{id}.ubicacionTaxista` (GeoPoint) y `fechaUbicacion` | `taxista/RastreoUbicacionTaxista.java`, `taxista/MapaTaxista.java`, `ServicioTaxiRepository.actualizarUbicacion` |
| Mapa del taxista | Pantallas *En camino* y *En traslado*: mapa con su punto azul, el hotel (recojo) y el aeropuerto (destino). Pide el permiso de ubicación | `taxista/EnCaminoActivity.java`, `taxista/EnTrasladoActivity.java`, layouts `activity_en_camino.xml`, `activity_en_traslado.xml` |
| Mapa del cliente | Pantalla de seguimiento: taxi (amarillo), hotel (celeste) y aeropuerto (rojo). El taxi se mueve con el listener de Firestore | `cliente/SeguimientoTaxiActivity.java`, `activity_seguimiento_taxi.xml` |
| Lógica común del mapa | Marcadores, encuadre de cámara (taxi + punto al que va) y búsqueda de coordenadas por texto con `Geocoder` (dirección del hotel y nombre del aeropuerto) | `utils/MapaServicioTaxi.java`, `utils/MapaHelper.java` |
| Seguridad | Regla nueva: solo el taxista asignado escribe `ubicacionTaxista` y `fechaUbicacion`, y solo con el servicio en curso. **Ya publicada** en la consola | `firestore.rules` |
| Configuración | Dependencias `play-services-maps` 19.0.0 y `play-services-location` 21.3.0; la clave de Maps se lee de `MAPS_API_KEY` en `local.properties` (no se versiona) | `gradle/libs.versions.toml`, `app/build.gradle`, `AndroidManifest.xml` |

### Cómo se verificó

- `./gradlew assembleDebug testDebugUnitTest`: compila y pasan las pruebas unitarias.
- La app se instaló y abrió en el emulador (Pixel 4, API 34) con el `google-services.json` del
  equipo; el inicio de sesión con Google funciona.
- **No verificado todavía:** que el mapa cargue y que el taxi se mueva. La cuenta de prueba de
  taxista quedó *pendiente de aprobación* y nadie con Superadmin la aprobó a tiempo.

### Para probarlo (cualquier integrante)

1. Poner `MAPS_API_KEY=...` en `local.properties` y `google-services.json` en `app/`
   (ver [`manual-instalacion.md`](manual-instalacion.md)).
2. El taxista de prueba debe estar **aprobado en el portal web** de taxistas (con el mismo correo de su cuenta de
   Firebase) y, por ahora, también en la app (*Superadmin → Taxistas → detalle → Aprobar*): ver la limitación
   en [`integracion-api-taxistas.md`](integracion-api-taxistas.md). Además, `TAXI_API_CORREO` y
   `TAXI_API_PASSWORD` en `local.properties`.
3. Taxista: *Simular nuevo pedido* → aceptar → permitir ubicación. Debe verse el mapa con calles.
4. En el emulador, *Extended controls → Location* para mover al taxista; en el teléfono del
   cliente el marcador del taxi se mueve en unos 10 s.
5. Si el mapa sale gris: en Logcat filtrar `Google Maps`. `Authorization failure` = falta la SHA-1
   de esa PC en la clave de Maps (o compartir el `debug.keystore` del equipo).

## 2. Pendiente para el 85 % y el cierre del proyecto

| # | Pendiente | Estado | Notas |
|---|---|---|---|
| 1 | Probar en dispositivo el mapa, el rastreo y la API | Bloqueado | Falta una cuenta de taxista aprobada (portal web + app), la credencial `APP_MOVIL` en `local.properties` y volver a publicar `firestore.rules` (campo `taxistaApiId`) |
| 2 | **Firebase Storage** (Clase 11): fotos del hotel (mínimo 4), habitaciones, servicios y foto de perfil | No iniciado | Hoy las fotos de hotel son imágenes incluidas en la app. Falta decidir si se usa Firebase Storage o el servicio de AWS que ofreció el curso |
| 3 | **API de taxistas con Retrofit** (Clase 12) | Hecho, sin probar en dispositivo | Integrado desde `lab7-api-taxistas`: login del taxista según la API, disponibilidad, datos del taxista y aviso de *API no disponible*. Detalle en [`integracion-api-taxistas.md`](integracion-api-taxistas.md). Pendiente: sincronizar la aprobación del portal con Firestore |
| 4 | **QR real** con la cámara | Simulado | `EscaneoActivity` usa el id del servicio en vez de leer el QR (`TODO lab 7`) |
| 5 | Calificar al taxista al finalizar el servicio | Hecho, sin probar en dispositivo | Estrellas en el seguimiento del cliente al quedar `FINALIZADO` (`POST /api/calificaciones`) |
| 6 | Ubicación del hotel en un mapa (detalle del hotel y configuración del admin) | No iniciado | Hoy la ubicación del hotel es solo texto |
| 7 | Ubicación del taxista con la pantalla apagada | Mejora | Ahora se reporta solo con la pantalla del servicio visible; para segundo plano hace falta un *foreground service* de tipo `location` |
| 8 | Notificaciones entre dispositivos (FCM): cobro, taxi asignado, alerta de checkout | No iniciado | Hoy son notificaciones locales |
| 9 | Desplegar el servicio de taxistas en la nube | Hecho | Gateway en Cloud Run (`TAXI_API_URL` por defecto) |
| 10 | Documentos finales: esquema de BD, arquitectura, OPEX, manuales | Parcial | Actualizar con lo de Lab 7 |
| 11 | Error de lint que rompe el CI | Pendiente | `res/layout/campo_tipo_documento.xml:36` usa `android:tint`; debe ser `app:tint` |
| 12 | Ramas del repositorio | Pendiente | `main` está desactualizada y no comparte historia con `firebase-crud`; acordar cuál queda como rama principal |

## 3. Configuración compartida del equipo

- **Clave de Google Maps:** la creó el dueño del proyecto de Firebase, restringida a
  `com.example.roomtrip` y a las SHA-1 registradas. Se pasa por privado y va en `local.properties`.
- **SHA-1:** cada PC firma con su propio `debug.keystore`. Para no registrar la de cada integrante,
  el equipo puede usar un mismo `debug.keystore` compartido por privado (nunca en el repositorio).
