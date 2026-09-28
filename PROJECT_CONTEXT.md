# Contexto del Proyecto: RoomTrip (Android App)

Este documento contiene la información técnica, reglas de arquitectura y estándares de configuración del proyecto **RoomTrip**. Está diseñado para que cualquier desarrollador o Asistente de IA (ChatGPT, Claude, Copilot, Gemini, etc.) tenga el contexto completo y trabaje de manera coherente con el equipo.

---

## 📌 1. Información General
* **Nombre del Proyecto:** RoomTrip
* **Application ID / Package:** `com.example.roomtrip`
* **Tipo de Proyecto:** Aplicación nativa Android
* **Lenguajes:** Java / Kotlin
* **Módulo Principal:** `:app`

---

## ⚙️ 2. Entorno de Compilación y Versiones Estandarizadas (CRÍTICO)

Para garantizar la compatibilidad entre todos los miembros del equipo (múltiples computadoras con distintas versiones de Android Studio), el entorno está estrictamente estandarizado:

| Herramienta / Configuración | Versión Estandarizada | Archivo de Configuración |
| :--- | :--- | :--- |
| **Android Gradle Plugin (AGP)** | `8.7.3` | `gradle/libs.versions.toml` |
| **Gradle Wrapper** | `8.10.2` | `gradle/wrapper/gradle-wrapper.properties` |
| **Java Daemon / Toolchain** | JDK `21` | `gradle/gradle-daemon-jvm.properties` |
| **Compile SDK** | `35` | `app/build.gradle` |
| **Target SDK** | `35` | `app/build.gradle` |
| **Min SDK** | `26` (Android 8.0+) | `app/build.gradle` |

> ⚠️ **REGLA DE ORO PARA LA IA / DESARROLLADORES:**
> **NO** sugerir ni aplicar actualizaciones automáticas de AGP (a versiones Canary/Preview como AGP 9.x) ni cambiar la versión de Gradle Wrapper a versiones no soportadas por la versión estable de Android Studio. Si Android Studio muestra el mensaje de *AGP Upgrade Assistant*, seleccionar **"Don't ask again for this project"**.

---

## 🛠️ 3. Dependencias y Librerías Clave

El proyecto utiliza un Version Catalog (`gradle/libs.versions.toml`). Las librerías principales incluyen:

* **UI & Layouts:** AndroidX AppCompat, ConstraintLayout, RecyclerView, CardView.
* **Material Design:** `com.google.android.material:material`
* **Navegación:** Android Jetpack Navigation Component (`navigation-fragment-ktx`, `navigation-ui-ktx`).
* **Carga de Imágenes:** Glide (`com.github.bumptech.glide:glide`).
* **Procesamiento de QR:** ZXing (`com.google.zxing:core`).
* **Binding:** ViewBinding habilitado en `app/build.gradle` (`buildFeatures { viewBinding = true }`).

---

## 📱 4. Estructura y Módulos de la Aplicación

La aplicación **RoomTrip** abarca los siguientes módulos funcionales:

### A. Usuario / Cliente (Reservas y Hoteles)
* **Bienvenida y Registro:** `ActivityBienvenida`, `ActivityRegistro`, `ActivityPerfil`.
* **Búsqueda y Reservas:** `ActivityBuscarHotel`, `ActivityDetalleHotel`, `ActivityPagoReserva`, `ActivityMisReservas`, `ActivityCheckout`.
* **Acceso y QR:** `ActivityMostrarQr` (generación y visualización de QR para check-in/reservas).

### B. Servicio de Transporte / Taxi Integrado
* **Solicitud y Monitoreo:** `ActivityBuscandoTaxista`, `ActivitySeguimientoTaxi`.
* **Comunicación:** `ActivityChat` (chat en tiempo real con el taxista).

### C. Módulo SuperAdmin (Administración Global)
* **Autenticación:** `ActivitySuperadminLogin`.
* **Dashboard y Métricas:** `ActivitySuperadminDashboard`, `ActivitySuperadminReportes`, `ActivitySuperadminAuditoria`.
* **Gestión de Entidades:**
  * **Hoteles:** `ActivitySuperadminHoteles`, `ActivitySuperadminRegistrarHotel`, `ActivitySuperadminDetalleHotel`, `ActivitySuperadminDetalleHotelReporte`.
  * **Usuarios y Taxistas:** `ActivitySuperadminUsuarios`, `ActivitySuperadminDetalleUsuario`, `ActivitySuperadminTaxistas`.

---

## 📐 5. Reglas de Desarrollo e Interfaz (Guía para la IA)

1. **ViewBinding:** Siempre preferir el uso de ViewBinding sobre `findViewById`.
2. **Compatibilidad:** Todos los componentes UI deben usar atributos de `colors.xml` y `themes.xml` para mantener coherencia estética.
3. **Control de Versiones (Git):**
   * **Archivos ignorados:** `local.properties`, `.gradle/`, `.idea/` y `build/` están excluidos en `.gitignore`.
   * **`local.properties`:** No se sube al repositorio; Android Studio lo genera automáticamente en cada equipo con la ruta de SDK local.

---

## 🚀 6. Primeros Pasos para Clonar y Compilar

1. Clonar el repositorio.
2. Abrir la carpeta raíz en Android Studio.
3. Permitir que Gradle descargue las dependencias y sincronice el proyecto.
4. Si se solicita seleccionar un JDK, asegúrese de seleccionar **JDK 21**.
5. ¡Listo para compilar y ejecutar!
