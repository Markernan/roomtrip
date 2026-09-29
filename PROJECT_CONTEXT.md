# Contexto del Proyecto: RoomTrip (Android App)

Este documento contiene la información técnica, reglas de arquitectura y estándares de configuración del proyecto **RoomTrip**. Está diseñado para que cualquier desarrollador o Asistente de IA (ChatGPT, Claude, Copilot, Gemini, etc.) tenga el contexto completo y trabaje de manera coherente con el equipo.

---

## 📌 1. Información General
* **Nombre del Proyecto:** RoomTrip
* **Application ID / Package:** `com.example.roomtrip`
* **Tipo de Proyecto:** Aplicación nativa Android
* **Lenguaje:** Java (restricción del enunciado: nada de Kotlin, Flutter ni React Native)
* **Módulo Principal:** `:app`
* **Estado:** la app funciona con **datos de prueba** (`data/MockData.java`). No usa todavía Firebase, Retrofit, almacenamiento local ni notificaciones (Labs 5, 6 y 7).

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

---

## 📱 4. Estructura y Módulos de la Aplicación

La aplicación **RoomTrip** tiene un único login (`login/LoginActivity`): se elige el rol y se navega a su pantalla. Los paquetes están en `app/src/main/java/com/example/roomtrip/`.

### A. Cliente (`cliente/`)
* **Acceso y perfil:** `RegistroActivity`, `PerfilActivity`. `BienvenidaActivity` es el login original con correo y contraseña; hoy nadie la abre y se conserva porque Firebase Authentication la va a necesitar (Lab 6).
* **Búsqueda y reservas:** `HomeActivity`, `BuscarHotelActivity`, `DetalleHotelActivity`, `PagoReservaActivity`, `ReservaExitosaActivity`, `MisReservasActivity`, `CheckoutActivity`.
* **Servicio de taxi:** `BuscandoTaxistaActivity`, `SeguimientoTaxiActivity`, `MostrarQrActivity` (genera el QR con ZXing).
* **Comunicación:** `ChatActivity` (chat privado cliente–hotel, disponible solo durante la reserva activa).

### B. Administrador de hotel (`adminhotel/`)
Una sola `MainActivity` con `Fragment` y `nav_graph` (Navigation Component): `InicioFragment`, `InventarioFragment` (habitaciones y servicios), `ChatsFragment`, `NotificacionesFragment`, `ConfiguracionHotelFragment`, `CheckoutsFragment`, `TrasladosEnCursoFragment`, `ValoracionesFragment`. `ReportesFragment` y `CheckoutHabitacionFragment` solo muestran su layout estático, sin lógica.

### C. Taxista (`taxista/`)
`SolicitadoActivity` (lista de pedidos), `EnCaminoActivity`, `EnTrasladoActivity`, `EscaneoActivity` (mockup del escaneo de QR), `FinalizadoActivity`, `PerfilTaxistaActivity`. El taxista se registra en el servicio web, no en la app.

### D. SuperAdmin (`superadmin/`)
`DashboardActivity`, `HotelesActivity`, `RegistrarHotelActivity`, `DetalleHotelAdminActivity`, `DetalleHotelReporteActivity`, `UsuariosActivity`, `DetalleUsuarioActivity`, `TaxistasActivity`, `ReportesActivity`, `AuditoriaActivity`.

### E. Datos y utilidades
* `data/model/`: modelos (`Hotel`, `Traslado`, `PedidoTaxi`, `Usuario`...) y los `enum` de estado.
* `data/MockData.java`: **todos** los datos de prueba. Cada método devuelve una lista nueva y modificable.
* `utils/`: `Constants` (roles y claves de `Intent`), helpers de navegación e insets.

---

## 📐 5. Reglas de Desarrollo e Interfaz (Guía para la IA)

1. **ViewBinding:** Siempre preferir el uso de ViewBinding sobre `findViewById`. Nunca buscar vistas por nombre con `getIdentifier()`. Hoy lo usan casi todo el admin de hotel, el flujo de pedidos del taxista y Registrar hotel; cliente y superadmin siguen con `findViewById` y se migran de forma progresiva.
2. **Compatibilidad:** Todos los componentes UI deben usar atributos de `colors.xml` y `themes.xml` para mantener coherencia estética.
3. **Estados:** usar los `enum` de `data/model/` (`EstadoServicioTaxi`, `EstadoReserva`, `EstadoCuenta`), nunca textos sueltos ("En Curso", "Activo"). El nombre del valor es el que se guarda y el que viaja por la API; el texto para el usuario sale de `getEtiqueta()`.
4. **Datos de prueba:** agregarlos en `MockData`, no dentro de una Activity o Fragment.
5. **Errores:** no usar `catch (Exception ignored) {}`. Un error que se traga sin avisar esconde bugs.
6. **Roles:** usar las constantes de `utils/Constants`, no textos sueltos.
7. **Control de Versiones (Git):**
   * **Archivos ignorados:** `local.properties`, `.gradle/`, `.idea/` y `build/` están excluidos en `.gitignore`.
   * Todo PR debe pasar el CI (GitHub Actions: pruebas unitarias, compilación y lint).
8. **Uso de IA:** declararlo en `docs/declaracion-ia.md` (exigencia del sílabo).

---

## 🚀 6. Primeros Pasos para Clonar y Compilar

1. Clonar el repositorio.
2. Abrir la carpeta raíz en Android Studio.
3. Permitir que Gradle descargue las dependencias y sincronice el proyecto.
4. Si se solicita seleccionar un JDK, basta con el que trae Android Studio (*Embedded JDK / jbr*).
5. Ejecutar sobre un dispositivo o emulador con Android 14 (API 34) o superior.

Por línea de comandos: `./gradlew testDebugUnitTest assembleDebug lintDebug`. Si `lintDebug` falla con un error interno de `AndroidLintWorkAction`, usar JDK 17 o 21 (el CI usa 21).
