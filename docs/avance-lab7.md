# Avance y pendientes — Lab 7 (presentación prefinal 85 %)

Estado al **17/11/2026**, rama `firebase-crud` (incluye la API de taxistas de `lab7-api-taxistas`). Este documento lista lo que ya está en el
repositorio para el Lab 7, cómo se verificó y lo que falta para cerrar el proyecto.

## 0. Estado para la presentación del 85 % (17/11/2026)

**Listo y verificado:** la rama compila (`assembleDebug`) y pasan todas las pruebas unitarias
(`testDebugUnitTest`, incluidas las 26 de `data/api`). La app se instala en el emulador y el login con Google
funciona con el `google-services.json` del equipo. Las reglas de Firestore con `taxistaApiId` ya están
publicadas.

**Probado en el emulador (17/11/2026, cuenta de taxista aprobada en el portal y en la app):** login del taxista
según la API, pedido simulado → aceptar → *En camino* → *Llegué al encuentro* → *En traslado* → escaneo →
*Trayecto finalizado*. El mapa carga con la clave de Maps, ubica el hotel y el aeropuerto con el geocoder, muestra
el taxi y lo mueve al cambiar el GPS del emulador; la ubicación se guarda en Firestore sin errores de reglas. El
lector QR abre la cámara (en el emulador no hay QR real; se cerró el servicio con la lectura simulada de depuración).

Arreglos que salieron de la prueba:
- La cámara del mapa quedaba alejada (nivel continente) si la primera posición del GPS era mala, como la del
  emulador antes de fijarla; ahora reencuadra cuando el taxi se mueve más de 1 km (`MapaServicioTaxi`).
- El lector QR se abría en horizontal; ahora sigue la orientación del teléfono (`AndroidManifest.xml`).

**Flujo del cliente probado (17/11/2026, dos emuladores: cliente y taxista a la vez):** registro de un cliente nuevo,
pestaña *Ubicación* del hotel, reserva de la Junior Suite (S/ 220, pago simulado), checkout con valoración,
*Solicitar taxi* (supera el mínimo de S/ 200) → el taxista lo acepta → en *Ver mi taxi* el cliente ve el taxi
moverse en el mapa, los datos de la API (vehículo, placa y valoración), el QR y, al finalizar, califica al taxista
(la API pasó a "★ 5.0 (1)").

Arreglos que salieron de esta prueba:
- *Ver mi taxi* perdía el vehículo, la placa y la valoración a los ~10 s: cada actualización de ubicación
  volvía a pintar solo el nombre. Ahora se guarda el texto de la API (`SeguimientoTaxiActivity`).
- Pestaña *Ubicación*: los lugares cercanos caían encima del hotel (se buscaban con la calle del hotel) y el mapa
  quedaba en el zoom máximo. Ahora se buscan con el distrito y la ciudad, y el encuadre tiene un zoom mínimo
  (`UbicacionHotel.zonaDe`, `MapaHelper.encuadrar`).
- *En camino* se cerraba sola a veces al aceptar un pedido: la caché local todavía no tenía el servicio
  aceptado (`ServicioTaxiRepository.escucharServicioEnCurso`).

**Mapa en *Buscando pasajeros*:** la pantalla principal del taxista ya no tiene el recuadro de ejemplo: muestra su
posición y el hotel de recojo de cada pedido disponible, y pide ahí el permiso de ubicación (`taxista/MapaPedidos.java`).
Probado en el emulador con un pedido simulado.

**Configuración para probar el flujo del taxi** (ya hecha para la cuenta a20220851@pucp.edu.pe):

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
- 17/11/2026: con la cuenta de taxista ya aprobada, el mapa carga, ubica hotel y aeropuerto, y el taxi se
  mueve al cambiar la ubicación del emulador (*Extended controls → Location* o `adb emu geo fix <lng> <lat>`).

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
| 1 | Probar en dispositivo el mapa, el rastreo y la API | Hecho | Taxista y cliente probados en el emulador el 17/11 (ver sección 0). Falta solo leer un QR real con la cámara de un teléfono |
| 2 | **Firebase Storage** (Clase 11): fotos del hotel (mínimo 4) y foto de perfil | Hecho; bucket creado (`us-east1`) y `storage.rules` publicadas el 17/11. Probado en emulador; falta una prueba con el administrador real | El administrador sube las fotos en *Configuración del hotel* (`hoteles/{id}/…jpg`); sus URLs van en `hoteles/{id}.fotos` y el formulario no guarda con menos de 4 (`Hotel.FOTOS_MINIMAS`). El cliente ve la primera foto en el listado y el detalle (con la imagen incluida como respaldo). La foto de perfil se copia a `perfiles/{uid}.jpg` y su URL a `usuarios/{uid}.fotoUrl`. Pasos de consola en [`manual-instalacion.md`](manual-instalacion.md), sección *Storage* |
| 3 | **API de taxistas con Retrofit** (Clase 12) | Hecho, sin probar en dispositivo | Integrado desde `lab7-api-taxistas`: login del taxista según la API, disponibilidad, datos del taxista y aviso de *API no disponible*. Detalle en [`integracion-api-taxistas.md`](integracion-api-taxistas.md). Pendiente: sincronizar la aprobación del portal con Firestore |
| 4 | **QR real** con la cámara | Hecho; la cámara abre en el emulador, falta leer un QR real en un teléfono | `EscaneoActivity` lee el QR del cliente con `zxing-android-embedded`. En compilaciones de depuración, una pulsación larga del botón simula la lectura (emulador sin cámara) |
| 5 | Calificar al taxista al finalizar el servicio | Hecho y probado (17/11) | Estrellas en el seguimiento del cliente al quedar `FINALIZADO` (`POST /api/calificaciones`) |
| 6 | Ubicación del hotel en un mapa | Hecho (cliente) y probado (17/11) | Pestaña *Ubicación* en el detalle del hotel: dirección, mapa (lite mode) con el hotel y los lugares históricos cercanos. Falta un selector de ubicación en el mapa para el admin (hoy la escribe como texto) |
| 7 | Ubicación del taxista con la pantalla apagada | Hecho, sin probar en dispositivo | `taxista/UbicacionTaxistaService` (servicio en primer plano de tipo `location`, con notificación fija). Lo arranca `MapaTaxista` desde la pantalla visible y se detiene solo cuando el servicio deja de estar en curso. Permisos `FOREGROUND_SERVICE` y `FOREGROUND_SERVICE_LOCATION` |
| 8 | Notificaciones entre dispositivos (FCM): cobro, taxi asignado, alerta de checkout | No iniciado | Hoy son notificaciones locales |
| 9 | Desplegar el servicio de taxistas en la nube | Hecho | Gateway en Cloud Run (`TAXI_API_URL` por defecto) |
| 10 | Documentos finales: esquema de BD, arquitectura, OPEX, manuales | Parcial | Actualizar con lo de Lab 7 |
| 11 | Error de lint que rompe el CI | Hecho | `campo_tipo_documento.xml` usa `app:tint`; `lintDebug` pasa sin errores |
| 12 | Ramas del repositorio | Pendiente | `main` está desactualizada y no comparte historia con `firebase-crud`; acordar cuál queda como rama principal |

## 3. Configuración compartida del equipo

- **Clave de Google Maps:** la creó el dueño del proyecto de Firebase, restringida a
  `com.example.roomtrip` y a las SHA-1 registradas. Se pasa por privado y va en `local.properties`.
- **SHA-1:** cada PC firma con su propio `debug.keystore`. Para no registrar la de cada integrante,
  el equipo puede usar un mismo `debug.keystore` compartido por privado (nunca en el repositorio).

## 4. Cierre del módulo cliente (rama `cliente-lab7`)

Auditoría de los requerimientos del cliente contra el código, sobre `firebase-crud`:

| Requerimiento | Resultado |
|---|---|
| RF-CL-01 a RF-CL-07, RF-GN-01, RF-GN-02, RF-GN-04 | Ya cubiertos (registro, búsqueda, reserva sin cruces, historial, checkout obligatorio, chat solo con la reserva activa). |
| RF-CL-08 | Cubierto: el taxi gratis solo aparece si el total alcanza el monto mínimo del hotel y si no se pidió antes. |
| RF-CL-09 | **Completado:** faltaba la **valoración del taxista** en el seguimiento. Ahora se muestra `★ 4.8 (14)` o *Sin calificaciones todavía* (`TaxistaApi.descripcionValoracion`). |
| RF-CL-10, RF-CL-11 | Cubiertos (QR del servicio y calificación al pasar a `FINALIZADO`). |
| RNF-06 | **Completado:** si la API de taxistas falla al cargar los datos del taxista, antes solo se escribía en el log. Ahora el cliente ve un aviso rojo (`tvAvisoApi`) y el resto de la pantalla sigue funcionando. |

Verificación: `assembleDebug`, `testDebugUnitTest` (91 pruebas, 0 fallos; 2 nuevas en `TaxistaApiTest`) y
`lintDebug` pasan. **Sin probar en dispositivo** (necesita las claves y cuentas del punto 0).

**Servicio en primer plano (pendiente 7).** `RastreoUbicacionTaxista` guarda ahora su último envío en un campo
compartido, de modo que la pantalla y el servicio no escriban a la vez en Firestore. Para probarlo: con un servicio
en curso, apagar la pantalla unos 30 s y comprobar en la consola de Firestore que `fechaUbicacion` sigue cambiando.
