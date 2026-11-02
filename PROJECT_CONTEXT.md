# Contexto del Proyecto: RoomTrip (Android App)

Este documento contiene la información técnica, reglas de arquitectura y estándares de configuración del proyecto **RoomTrip**. Está diseñado para que cualquier desarrollador o Asistente de IA (ChatGPT, Claude, Copilot, Gemini, etc.) tenga el contexto completo y trabaje de manera coherente con el equipo.

---

## 📌 1. Información General
* **Nombre del Proyecto:** RoomTrip
* **Application ID / Package:** `com.example.roomtrip`
* **Tipo de Proyecto:** Aplicación nativa Android
* **Lenguaje:** Java (restricción del enunciado: nada de Kotlin, Flutter ni React Native)
* **Módulo Principal:** `:app`
* **Estado:** los **cuatro roles** usan Firebase Authentication (FirebaseUI) y Cloud Firestore desde el Lab 6. `data/MockData.java` queda para los datos de demostración que carga el Superadmin y para pantallas aún sin backend. Retrofit (API de taxistas) llega en el Lab 7 y Google Maps en el Lab 7.

---

## ⚙️ 2. Entorno de Compilación y Versiones Estandarizadas (CRÍTICO)

Para garantizar la compatibilidad entre todos los miembros del equipo (múltiples computadoras con distintas versiones de Android Studio), el entorno está estrictamente estandarizado:

| Herramienta / Configuración | Versión Estandarizada | Archivo de Configuración |
| :--- | :--- | :--- |
| **Android Gradle Plugin (AGP)** | `8.7.3` | `gradle/libs.versions.toml` |
| **Gradle Wrapper** | `8.10.2` | `gradle/wrapper/gradle-wrapper.properties` |
| **JDK para ejecutar Gradle** | El que trae Android Studio (cualquier JDK 17 o superior). No se fija: el bytecode lo define `compileOptions` (Java 17) | `app/build.gradle` |
| **Android Studio** | Ladybug (2024.2.1) o superior, que es la que corresponde a AGP 8.7 | — |
| **Compile SDK** | `35` (el máximo que soporta AGP 8.7) | `app/build.gradle` |
| **Target SDK** | `35` | `app/build.gradle` |
| **Min SDK** | `34` (Android 14 — exigido por el enunciado, RES-02) | `app/build.gradle` |

> `local.properties` **no se versiona**: Android Studio lo genera en cada equipo con su propia ruta del SDK.

> ⚠️ **REGLA DE ORO PARA LA IA / DESARROLLADORES:**
> **NO** sugerir ni aplicar actualizaciones automáticas de AGP (a versiones Canary/Preview como AGP 9.x) ni cambiar la versión de Gradle Wrapper a versiones no soportadas por la versión estable de Android Studio. Si Android Studio muestra el mensaje de *AGP Upgrade Assistant*, seleccionar **"Don't ask again for this project"**.
> Tampoco subir `compileSdk` por encima de 35 ni volver a poner parches (`suppressUnsupportedCompileSdk`, versiones forzadas): ya se quitaron.

---

## 🛠️ 3. Dependencias y Librerías Clave

El proyecto utiliza un Version Catalog (`gradle/libs.versions.toml`). Las librerías principales incluyen:

* **UI & Layouts:** AndroidX AppCompat, ConstraintLayout, RecyclerView, CardView.
* **Material Design:** `com.google.android.material:material`
* **Navegación:** Android Jetpack Navigation Component 2.7.7 (`navigation-fragment-ktx`, `navigation-ui-ktx`). No subir a 2.8+: da el error de `KSerializer` que ya se resolvió.
* **Carga de Imágenes:** Glide (`com.github.bumptech.glide:glide`).
* **Procesamiento de QR:** ZXing (`com.google.zxing:core`).
* **Binding:** ViewBinding habilitado en `app/build.gradle` (`buildFeatures { viewBinding true }`).
* **Pruebas:** JUnit 4 para pruebas unitarias (`app/src/test`).
* **Firebase (Lab 6):** BoM `34.13.0` con `firebase-auth` y `firebase-firestore`, FirebaseUI `firebase-ui-auth:9.1.1` y el plugin `com.google.gms.google-services` `4.4.2` (las versiones de la Clase 10). Son compatibles con AGP 8.7.3 y compileSdk 35: **no** subirlas sin comprobar que no exijan compileSdk 36. En FirebaseUI 9.x `setIsSmartLockEnabled(false)` ya no existe: se usa `setCredentialManagerEnabled(false)`.
* **`google-services.json`:** no se versiona (`.gitignore`). Cada integrante lo descarga de la consola y el CI lo crea desde el secreto `GOOGLE_SERVICES_JSON`. Sin ese archivo el proyecto **no compila** (ver `docs/manual-instalacion.md`, sección 5).

---

## 📱 4. Estructura y Módulos de la Aplicación

La aplicación **RoomTrip** tiene un único login (`login/LoginActivity`): FirebaseUI (correo o Google) y verificación de correo; luego lee `usuarios/{uid}` y abre el módulo del rol (`entrarSegunRol`). Si la cuenta no tiene perfil: busca una invitación de admin de hotel (`invitaciones/{correo}`) o pregunta *Soy cliente* / *Soy taxista*. El Superadmin se crea a mano en la consola. Los paquetes están en `app/src/main/java/com/example/roomtrip/`.

### A. Cliente (`cliente/`)
* **Acceso y perfil:** `RegistroActivity` (cuenta de Firebase Auth + `usuarios/{uid}`, o modo "completar" tras entrar con Google), `PerfilActivity` (Firestore; la foto sigue en el teléfono con `PerfilClienteRepository`). `BienvenidaActivity` solo redirige al login único.
* **Búsqueda y reservas:** `HomeActivity`, `BuscarHotelActivity`, `DetalleHotelActivity` (habitación, fechas y disponibilidad), `PagoReservaActivity` (tarjeta simulada; crea la reserva con una transacción que bloquea noches superpuestas), `ReservaExitosaActivity`, `MisReservasActivity` (listener + `AvisosCobro`), `CheckoutActivity` (valoración, checkout PENDIENTE y taxi gratis).
* **Servicio de taxi:** `BuscandoTaxistaActivity` y `SeguimientoTaxiActivity` escuchan el documento de `serviciosTaxi`; `MostrarQrActivity` genera con ZXing el QR con el id del servicio.
* **Comunicación:** `ChatActivity` (subcolección `reservas/{id}/mensajes`; la usan el cliente y el hotel, solo con la reserva activa).

### B. Administrador de hotel (`adminhotel/`)
Una sola `MainActivity` con `Fragment` y `nav_graph` (Navigation Component): `InicioFragment`, `InventarioFragment` (habitaciones y servicios), `ChatsFragment`, `NotificacionesFragment`, `ConfiguracionHotelFragment`, `CheckoutsFragment`, `TrasladosEnCursoFragment`, `ValoracionesFragment`. `ReportesFragment` muestra los ingresos por servicios adicionales ordenados de menor a mayor (`IngresoServicio.ordenarDeMenorAMayor`) y `CheckoutHabitacionFragment` valida el cobro adicional por daños con `CobroAdicional` (RN-012) y cobra con una transacción. Todo lee y escribe en Firestore filtrando por el `hotelId` del administrador (`AdminSession`); `VistasAdmin` convierte los documentos a los modelos que ya usaban los adapters.

### C. Taxista (`taxista/`)
`SolicitadoActivity` (pedidos en tiempo real), `EnCaminoActivity`, `EnTrasladoActivity`, `EscaneoActivity` (valida el QR contra Firestore; la lectura con cámara aún es simulada), `FinalizadoActivity`, `PerfilTaxistaActivity`, `HistorialServiciosActivity`.
* **Login (Lab 6):** solo entra si `rol = TAXISTA`, `estado = ACTIVO` y `estadoAprobacion = APROBADO`. La primera vez (*Soy taxista*) crea la solicitud en `PENDIENTE`.
* **Datos (Lab 6):** `data/firebase/ServicioTaxiRepository` (listeners, transacciones para aceptar y avanzar estados, cierre por QR, historial) y `UsuarioRepository`. `SesionTaxistaManager` (SharedPreferences) guarda el uid, el nombre y la disponibilidad.

### D. SuperAdmin (`superadmin/`)
`DashboardActivity` (totales y *Cargar datos de demostración*), `HotelesActivity`, `RegistrarHotelActivity` (hotel + invitación del administrador en un batch), `DetalleHotelAdminActivity`, `DetalleHotelReporteActivity`, `UsuariosActivity`, `DetalleUsuarioActivity` (activar/desactivar, aprobar/rechazar taxistas), `TaxistasActivity`, `ReportesActivity`, `AuditoriaActivity` (colección `logs`). Todo sobre Firestore; `UsuarioVista` adapta `PerfilUsuario` a las listas existentes.

### E. Datos y utilidades
* `data/model/`: modelos. Los documentos de Firestore son `PerfilUsuario`, `Hotel`, `Habitacion`, `Servicio`, `Reserva`, `MensajeChat`, `ValoracionHotel` y `ServicioTaxi`; los demás (`Traslado`, `Checkout`, `Chat`...) son modelos de vista. También los `enum` de estado.
* `data/firebase/`: un repositorio por colección (`UsuarioRepository`, `HotelRepository`, `ReservaRepository`, `ChatRepository`, `ServicioTaxiRepository`, `LogRepository`) con callbacks `Callbacks.Exito`/`Callbacks.Fallo`. Las pantallas no llaman a Firestore directamente.
* `data/MockData.java`: datos de demostración (los que carga el Superadmin) y de pantallas aún sin backend.
* Las reglas de seguridad están en `firestore.rules` (raíz del repositorio) y cubren todas las colecciones.
* `utils/`: `Constants` (roles y claves de `Intent`), helpers de navegación e insets, `SesionFirebase` (uid del usuario o vuelta al login), `RegistroEventos` (colección `logs`) y `ErroresFirebase` (mensaje para el usuario según el error).

---

## 📐 5. Reglas de Desarrollo e Interfaz (Guía para la IA)

1. **ViewBinding:** Siempre preferir el uso de ViewBinding sobre `findViewById`. Nunca buscar vistas por nombre con `getIdentifier()`. Hoy lo usan casi todo el admin de hotel, el flujo de pedidos del taxista, Registrar hotel y el checkout y taxi del cliente; el resto de cliente y superadmin sigue con `findViewById` y se migra de forma progresiva.
   * **Quién migra qué:** cada integrante migra los layouts de su propio módulo (cliente, superadmin), tanto a ViewBinding como a `strings.xml`.
2. **Compatibilidad:** Todos los componentes UI deben usar atributos de `colors.xml` y `themes.xml` para mantener coherencia estética.
3. **Estados:** usar los `enum` de `data/model/` (`EstadoServicioTaxi`, `EstadoReserva`, `EstadoCuenta`, `EstadoCheckout`, `EstadoHabitacion`), nunca textos sueltos ("En Curso", "Activo"). El nombre del valor es el que se guarda y el que viaja por la API; el texto para el usuario sale de `getEtiqueta()`.
4. **Datos de prueba:** agregarlos en `MockData`, no dentro de una Activity o Fragment.
   * **Firestore:** los POJO llevan constructor vacío y getters/setters; los métodos de reglas de negocio **no** empiezan con `get`/`is` (si no, Firestore los guarda como campos). Los enums se guardan por su nombre. Los listeners se registran en `onStart` y se quitan en `onStop`.
5. **Errores:** no usar `catch (Exception ignored) {}`. Un error que se traga sin avisar esconde bugs.
6. **Roles:** usar las constantes de `utils/Constants`, no textos sueltos.
7. **Control de Versiones (Git):**
   * **Archivos ignorados:** `local.properties`, `.gradle/`, `.idea/` y `build/` están excluidos en `.gitignore`.
   * Todo PR debe pasar el CI (GitHub Actions: pruebas unitarias, compilación y lint).
8. **Textos:** todo texto visible va en `res/values/strings.xml` (con `tools:text` en el layout si se quiere previsualizar), no fijo en el XML ni en Java. Ya está así en admin de hotel y taxista; cliente y superadmin (~250 textos fijos) los migra su dueño. Los textos con datos usan placeholders (`%1$s`, `%1$.2f`).
9. **Uso de IA:** declararlo en `docs/declaracion-ia.md` (exigencia del sílabo).

---

## 🚀 6. Primeros Pasos para Clonar y Compilar

1. Clonar el repositorio.
2. Abrir la carpeta raíz en Android Studio.
3. Permitir que Gradle descargue las dependencias y sincronice el proyecto.
4. Si se solicita seleccionar un JDK, basta con el que trae Android Studio (*Embedded JDK / jbr*).
5. Ejecutar sobre un dispositivo o emulador con Android 14 (API 34) o superior.

Por línea de comandos: `./gradlew testDebugUnitTest assembleDebug lintDebug`. Si `lintDebug` falla con un error interno de `AndroidLintWorkAction`, usar JDK 17 o 21 (el CI usa 21).
