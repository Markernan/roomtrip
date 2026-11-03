# Manual de instalación — RoomTrip

> Borrador. La sección de Firebase corresponde al Lab 6; la de despliegue se completa en el Lab 7.

## 1. Requisitos previos

| Herramienta | Versión | Para qué |
|---|---|---|
| Android Studio | Ladybug (2024.2.1) o superior | App móvil. Es la versión que corresponde al Android Gradle Plugin 8.7.3 |
| JDK | El que trae Android Studio (17 o superior) | App móvil. No hace falta instalar otro |
| Emulador o dispositivo | Android 14 (API 34) o superior | Ejecutar la app |
| Git | 2.40 | Control de versiones |
| Docker Desktop | Vigente | Servicio de taxistas |
| JDK 21 y Maven | 21 y 3.9 o superior | Servicio de taxistas (compilar y probar) |
| Cuenta de Firebase | Proyecto creado | Desde el Lab 6 |
| Cuenta de MongoDB Atlas | Clúster M0 creado | Solo para desplegar el servicio de taxistas en la nube |

## 2. Clonar el repositorio

```bash
git clone https://github.com/Markernan/roomtrip.git
```

## 3. App móvil (`app/`)

1. Abrir la carpeta raíz del repositorio (`roomtrip/`) en Android Studio.
2. Sincronizar Gradle. Android Studio genera `local.properties` con la ruta de tu SDK
   (el archivo no se versiona).
3. Copiar `google-services.json` en la carpeta `app/` (sección 5). **Sin ese archivo el proyecto
   no compila**: el plugin `google-services` lo exige.
4. Ejecutar sobre un dispositivo o emulador con **API 34 o superior** y con Google Play
   (lo necesitan Firebase y el inicio con Google). La app necesita internet.

Por línea de comandos, desde la raíz:

```bash
./gradlew testDebugUnitTest   # pruebas unitarias
./gradlew assembleDebug       # genera app/build/outputs/apk/debug/app-debug.apk
./gradlew lintDebug           # análisis estático
```

Si `lintDebug` falla con un error interno de `AndroidLintWorkAction`, ejecutarlo con JDK 17 o 21.

## 4. Servicio de taxistas (`taxi-service/`)

El código está en la rama `web-taxis`. Ver [`taxi-service/README.md`](../taxi-service/README.md)
para el detalle. En resumen, desde la raíz de ese código:

1. Copiar `.env.example` a `.env` y completar `JWT_SECRET` (mínimo 32 caracteres) y las
   contraseñas iniciales.
2. `docker compose up --build` levanta MongoDB y los cuatro servicios.
3. Portal web en `http://localhost:8083` y API (gateway) en `http://localhost:8080`.

## 5. Configuración de Firebase

Lo hace **una sola persona** (el proyecto de Firebase es del equipo); los demás solo necesitan el
archivo `google-services.json`.

### 5.1 Proyecto y app Android (Clase 9.2)

1. Entrar a <https://console.firebase.google.com/> con la cuenta `@pucp.edu.pe` y crear el proyecto.
2. Agregar una app **Android** con el paquete **`com.example.roomtrip`** (no se puede cambiar después).
3. Descargar `google-services.json` y copiarlo en `app/`. **No se sube al repositorio** (está en
   `.gitignore`): se comparte por un canal privado del equipo.
4. Para el CI: en GitHub, *Settings › Secrets and variables › Actions*, crear el secreto
   **`GOOGLE_SERVICES_JSON`** y pegar el contenido completo del archivo. El flujo de GitHub Actions
   lo escribe en `app/` antes de compilar.

Las versiones de Firebase ya están en `gradle/libs.versions.toml` (BoM 34.13.0, FirebaseUI 9.1.1,
plugin 4.4.2, las de la Clase 10).

### 5.2 Authentication (Clase 10)

1. *Authentication › Sign-in method*: habilitar **Correo electrónico/contraseña** y **Google**.
2. *Authentication › Configuración*: **desactivar la protección contra la enumeración de correos**.
   FirebaseUI la necesita desactivada para saber si el correo ya tiene cuenta.
3. Para el inicio con Google, registrar la huella **SHA-1** de cada computadora que compile la app:
   - En Android Studio: *Gradle › Tasks › android › signingReport* (o `./gradlew signingReport`).
   - Copiar el SHA-1 de la variante `debug` en *Configuración del proyecto › Tus apps › Agregar huella digital*.
   - **Volver a descargar** `google-services.json` (ahora incluye el cliente OAuth) y reemplazarlo en `app/`.
   - Si se omite este paso, la app igual funciona pero solo ofrece correo y contraseña.

### 5.3 Cloud Firestore (Clase 9.2)

1. *Firestore Database › Crear base de datos*, en **modo de producción**.
2. En la pestaña *Reglas*, pegar el contenido de [`firestore.rules`](../firestore.rules) y **Publicar**.
3. No hace falta crear índices: las consultas de la app usan un solo filtro.

Colecciones (todas las crea la app; no hay que crearlas a mano):

| Colección | Documento | Contenido |
|---|---|---|
| `usuarios` | `{uid}` de Authentication | Perfil y rol: `nombres`, `apellidos`, `correo`, `telefono`..., `rol` (`CLIENTE`/`ADMIN_HOTEL`/`TAXISTA`/`SUPERADMIN`), `estado` (`ACTIVO`/`INACTIVO`), `estadoAprobacion` (taxista), `hotelId` y `hotelNombre` (admin de hotel) |
| `invitaciones` | correo en minúsculas | Hotel asignado a un futuro administrador (`hotelId`, `hotelNombre`). La crea el Superadmin al registrar el hotel |
| `hoteles` | id autogenerado | Datos del hotel, `estado`, `calificacion`, `numValoraciones`, `montoMinimoTaxi`, `lugaresCercanos`. Subcolecciones `habitaciones` y `servicios` |
| `reservas` | id autogenerado | Cliente, hotel, habitación, fechas (`yyyy-MM-dd`), `montoTotal`, `estado`, `estadoCheckout`, valoración, cobro adicional. Subcolección `mensajes` (chat) |
| `ocupaciones` | `hab_<habitación>_<noche>` y `cli_<cliente>_<noche>` | "Candados" por noche: la transacción de la reserva falla si alguno ya existe (RN-001, RN-002) |
| `valoraciones` | id de la reserva | Opinión del cliente en el checkout |
| `serviciosTaxi` | id autogenerado (es el contenido del QR) | `estado` (`SOLICITADO`...`FINALIZADO`), cliente, hotel, `reservaId`, aeropuerto, taxista y fechas |
| `logs` | id autogenerado | `tipo`, `detalle`, `usuarioId`, `rol`, `fecha` (auditoría del Superadmin) |

### 5.4 Crear el Superadmin (una sola vez)

El Superadmin no se puede registrar desde la app (nadie podría darse ese rol a sí mismo):

1. En la app, **Iniciar sesión** con la cuenta que será Superadmin (correo o Google). Si es con
   correo, verificarlo. Cuando aparezca *Completa tu registro*, **cancelar**.
2. En la consola, *Authentication › Users*: copiar el **UID** de esa cuenta.
3. *Firestore › Iniciar colección* `usuarios` (o abrirla) › *Agregar documento* con **ID = ese UID**
   y estos campos de tipo *string*: `rol` = `SUPERADMIN`, `estado` = `ACTIVO`, `nombres`,
   `correo`.
4. Volver a iniciar sesión: entra al panel del Superadmin.

> **Tip para probar varios roles con un solo Gmail:** Gmail ignora lo que va después de un `+`.
> `tucorreo+cliente@gmail.com`, `tucorreo+hotel@gmail.com` y `tucorreo+taxi@gmail.com` son
> cuentas distintas en Firebase, pero todos los correos de verificación llegan a tu bandeja.

### 5.5 Probar el flujo completo

1. **Superadmin:** en el panel, **Cargar datos de demostración** (crea 3 hoteles con habitaciones
   y servicios, solo si aún no hay hoteles). Para un hotel con administrador, **Registrar hotel** e
   indicar el correo del administrador: se crea el hotel y una invitación para ese correo.
2. **Administrador de hotel:** iniciar sesión con el correo invitado (y verificarlo). La app
   encuentra la invitación, crea su perfil con el hotel asignado y abre su panel: configurar el
   hotel, agregar habitaciones y servicios.
3. **Cliente:** *Regístrate* desde el login (o iniciar sesión con Google y elegir *Soy cliente*).
   Buscar un hotel, elegir habitación y fechas (no deja superponer noches), pagar con una tarjeta
   simulada (cualquier número de 13 a 19 dígitos, `MM/AA` y CVC). En *Mis reservas* se puede
   chatear con el hotel y, desde el día de entrada, **hacer checkout** con valoración. Si el total
   alcanza el monto mínimo del hotel, aparece **Solicitar taxi**.
4. **Taxista:** iniciar sesión y elegir *Soy taxista*: queda pendiente. El **Superadmin** lo aprueba
   en *Taxistas*. Al volver a entrar ve los pedidos en tiempo real (también puede **Simular nuevo
   pedido**). Aceptar → *Iniciar ruta* → *Llegué al encuentro* → *Escanear QR* (el QR es el que
   muestra el cliente en *Ver mi taxi*; la lectura con cámara aún es simulada).
5. **Administrador de hotel:** en *Checkouts* cobra el checkout pendiente (con cobro adicional por
   daños si corresponde). Mientras el cliente tenga la app abierta, recibe la notificación del cobro.
6. **Superadmin:** *Auditoría* muestra los eventos de todos los roles y *Reportes* las reservas por hotel.

Si algo no aparece, revisar en *Firestore › Reglas* que se haya publicado la versión actual de
`firestore.rules`; un error de permisos se muestra en la app como "No tienes permiso para realizar esta acción."

## 6. Despliegue en la nube

- **Servicio de taxistas:** Google Cloud Run con MongoDB Atlas M0. El procedimiento está en
  `docs/DESPLIEGUE.md` de la rama `web-taxis`.
- **App móvil:** por completar.
