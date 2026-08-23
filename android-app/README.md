# android-app

App móvil Android nativa en **Java** de RoomTrip.

> Esta carpeta es un marcador. El proyecto todavía **no** ha sido generado.

## Cómo generarlo

No crear los archivos a mano. En Android Studio:

1. **New Project → Empty Views Activity**
2. *Language*: **Java**
3. *Minimum SDK*: **API 34 (Android 14)**
4. *Save location*: esta misma carpeta (`roomtrip/android-app`)

Eso genera el Gradle wrapper correcto y la estructura estándar.

## Después de generarlo

- Colocar `google-services.json` en `app/`. **No se versiona** — está en `.gitignore`.
- Verificar que `minSdk = 34` en `app/build.gradle`.
- Borrar este archivo y reemplazarlo por el README del proyecto Android si hace falta.

## Notas de diseño

- **Un solo login.** Tras autenticar se lee el campo `rol` del usuario y se navega al
  grafo de navegación del rol. No son cuatro apps.
- Todas las llamadas a la API de taxistas pasan por una única clase `TaxiRepository`.
