# Manual de instalación — RoomTrip

> Plantilla. Se completa conforme se estabiliza el despliegue.

## 1. Requisitos previos

| Herramienta | Versión mínima |
|---|---|
| Android Studio | Con soporte para API 34 |
| JDK | 17 |
| Node.js | 24 |
| Git | 2.40 |
| Cuenta de Firebase | Proyecto creado |
| Cuenta de MongoDB | Clúster creado |

## 2. Clonar el repositorio

```bash
git clone https://github.com/<usuario>/roomtrip.git
```

## 3. App móvil (`android-app/`)

1. Abrir la carpeta `android-app/` en Android Studio.
2. Descargar `google-services.json` desde la consola de Firebase
   (*Configuración del proyecto → Tus apps → Android*) y colocarlo en
   `android-app/app/`. **No está versionado: cada integrante lo descarga.**
3. Sincronizar Gradle.
4. Ejecutar sobre un dispositivo o emulador con **API 34 o superior**.

## 4. `taxi-service/`

1. `cd taxi-service && npm install`
2. Copiar `.env.example` a `.env` y completar la cadena de conexión de MongoDB.
3. `npm run dev` para desarrollo.

## 5. Configuración de Firebase

Por completar: creación del proyecto, habilitación de Authentication, despliegue de las
reglas de seguridad de Firestore y Realtime Database.

## 6. Despliegue en la nube

Por completar.
