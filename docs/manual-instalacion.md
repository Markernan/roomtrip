# Manual de instalación — RoomTrip

> Borrador. Las secciones de Firebase y de despliegue se completan en los Labs 6 y 7.

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
3. Ejecutar sobre un dispositivo o emulador con **API 34 o superior**.

La app funciona hoy con datos de prueba, así que **todavía no necesita** `google-services.json`
ni acceso a internet.

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

Por completar en el Lab 6: creación del proyecto, habilitación de Authentication, descarga de
`google-services.json` (se coloca en `app/`, no se versiona) y despliegue de las reglas de
seguridad de Firestore y Realtime Database.

## 6. Despliegue en la nube

- **Servicio de taxistas:** Google Cloud Run con MongoDB Atlas M0. El procedimiento está en
  `docs/DESPLIEGUE.md` de la rama `web-taxis`.
- **App móvil:** por completar.
