# Integración con la API de taxistas (Lab 7)

Cómo habla la app con el servicio web de taxistas (`roomtrip-taxistas-web`: gateway, taxistas y
calificaciones), y qué falta por conectar en las pantallas. El contrato completo de la API (rutas, roles,
errores) está en `docs/API.md` de ese repositorio; aquí solo lo que le toca a la app.

## 1. Qué va por Firebase y qué por la API

La app usa Firebase para casi todo. La API se usa para lo que el plan de proyecto dice que pertenece al
sistema web de gestión de taxistas:

| Firebase (Auth y Firestore) | API de taxistas (Retrofit) |
|---|---|
| Login de todos los roles, clientes, hoteles, reservas, chat | Registro y **aprobación** del taxista (la decide el Superadmin en el portal web) |
| El **pedido de taxi en tiempo real** (`serviciosTaxi`): estados, QR y asignación | Datos del taxista: foto, vehículo, placa, **valoración** |
| La **ubicación** del taxista durante el servicio (Lab 7) | **Disponibilidad** (`DISPONIBLE` / `EN_SERVICIO`) y lista de taxistas disponibles |
| | **Calificaciones** del taxista (`POST /api/calificaciones`) |

Se conectan por el **correo**: un taxista tiene una cuenta de Firebase y un registro en la API, y comparten
el mismo correo. `TaxiRepository.buscarPorCorreo` resuelve el id del taxista en la API a partir de él.

Consecuencia a tener presente: el taxista usa el mismo correo en el portal web y en la app, pero con **dos
contraseñas distintas** (una en cada sistema).

## 2. Configuración

En `local.properties` (no se versiona):

```
TAXI_API_URL=https://gateway-service-7oe536zvqa-uc.a.run.app   # opcional: este es el valor por defecto
TAXI_API_CORREO=app-movil@roomtrip.pe
TAXI_API_PASSWORD=...                                          # solo letras y números
```

La contraseña es la de la credencial de servicio `APP_MOVIL`. La administra quien desplegó la nube y se
pasa por privado. **Nunca** va al repositorio, a un chat ni a un documento.

Limitación conocida: esa credencial viaja dentro del APK, de donde puede extraerse. Por eso la API le
entrega a la app solo una **vista reducida** de los taxistas (sin DNI, domicilio ni fecha de nacimiento) y
limita los intentos de login.

## 3. Uso desde una pantalla

```java
TaxiRepository taxis = TaxiServicios.repositorio();

taxis.buscarPorCorreo(correoDeFirebase,
        taxista -> mostrar(taxista.nombreCompleto(), taxista.descripcionVehiculo(), taxista.getPlaca()),
        error -> avisar(error));
```

Los callbacks se ejecutan en el hilo principal y tienen la misma forma que los de los repositorios de
Firebase (`Callbacks.Exito` y `Callbacks.Fallo`). El cliente inicia sesión solo, guarda el token, lo agrega a
cada petición y lo renueva si vence.

| Método | Para qué |
|---|---|
| `taxistasDisponibles` | Taxistas aprobados y disponibles (RF-API-001) |
| `buscarPorCorreo` | Resolver al taxista desde su cuenta de Firebase |
| `obtenerTaxista` | Datos del taxista asignado |
| `cambiarDisponibilidad` | `TaxistaApi.EN_SERVICIO` al asignar un traslado, `DISPONIBLE` al terminarlo |
| `calificar` | Calificar al finalizar (un servicio se califica una sola vez) |
| `valoracion` | Valoración promedio |

## 4. Cuando la API no responde (RF-TAX-018)

Todo fallo llega como `ApiException`. Si `error.esNoDisponible()`, la pantalla muestra el aviso y **el resto
de la app sigue funcionando**: las reservas no dependen de la API.

```java
error -> {
    Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();   // texto ya listo para el usuario
    if (error.esNoDisponible()) { /* seguir sin los datos del taxista */ }
}
```

`esNoDisponible()` cubre: sin red, tiempo agotado y cualquier error 5xx. Tras un rato sin uso, el servicio
tarda **10 a 15 segundos** en despertar (escala a cero para no costar), así que la **primera** consulta puede
tardar; el cliente espera hasta 30 s antes de rendirse. Los demás tipos (`NO_ENCONTRADO`, `REGLA_NEGOCIO`,
`NO_CONFIGURADA`, `DEMASIADOS_INTENTOS`...) traen su propio mensaje.

## 5. Lo que ya está hecho y lo que falta

Hecho (`data/api/`, 26 pruebas): interfaz de Retrofit, modelos, cliente con autenticación automática,
`TaxiRepository`, traducción de errores y configuración desde `local.properties`.

**Falta conectar las pantallas** (cada cambio toca flujos que ya funcionan con Firebase, así que conviene
probarlos en dispositivo):

1. **Login del taxista** (`login/LoginActivity`): tras iniciar sesión con Firebase, consultar
   `buscarPorCorreo`. Decidir si la aprobación la manda la API (`estaAprobado()`) o sigue en Firestore.
2. **Cliente**: en `BuscandoTaxistaActivity` y `SeguimientoTaxiActivity`, mostrar foto, vehículo, placa y
   valoración del taxista asignado (el uid de Firebase → su correo → `buscarPorCorreo`).
3. **Taxista**: al aceptar un pedido (`SolicitadoActivity`), `cambiarDisponibilidad(EN_SERVICIO)`; al
   finalizar (`EscaneoActivity` / `FinalizadoActivity`), `DISPONIBLE`.
4. **Calificación** del taxista al finalizar el servicio, con `calificar`.
5. Registrar en `logs` los fallos `NO_DISPONIBLE`: `TaxiRepository` acepta un observador (`AlFallar`) para
   eso, y `RegistroEventos` ya existe.

## 6. Cómo probarlo

Desde Android Studio o por línea de comandos: `./gradlew testDebugUnitTest`. Las pruebas de `data/api` usan
un servidor simulado (MockWebServer) y no necesitan internet ni la credencial real.
