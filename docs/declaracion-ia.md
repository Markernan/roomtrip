# Declaración de uso de inteligencia artificial generativa

Curso **1TEL05 — Servicios y Aplicaciones para IoT**, PUCP, semestre 2026-2.
Proyecto **RoomTrip**.

El sílabo del curso exige que todo uso de herramientas de IA generativa se declare y se
cite. El profesor puede solicitar el registro de prompts como anexo, por lo que este
archivo se completa **conforme se produce cada aporte**, no al cierre del proyecto.

## Cómo registrar una entrada

Cada vez que se use una herramienta de IA generativa para producir algo que termine en
el repositorio (código, documentación, diagramas, textos), se agrega una fila a la tabla
y, si el prompt fue extenso o determinante, se transcribe en el anexo.

| # | Fecha | Herramienta | Propósito | Archivos afectados | Responsable | Verificación |
|---|---|---|---|---|---|---|
| 1 | 23/08/2026 | Claude (Anthropic) | Andamiaje inicial del repositorio: estructura de carpetas, `.gitignore`, `README.md` y plantillas de documentación | `README.md`, `.gitignore`, `docs/*` | Mark Valencia | Revisado y adaptado manualmente |
| 2 | 02/11/2026 | Claude (Anthropic) | Lab 6 del rol taxista: Firebase Authentication con FirebaseUI, Cloud Firestore (perfil, pedidos, estados, historial), reglas de seguridad y documentación | `app/build.gradle`, `gradle/libs.versions.toml`, `login/LoginActivity`, `taxista/**`, `data/model/{ServicioTaxi,RolUsuario,EstadoAprobacion}`, `utils/{RegistroEventos,ErroresFirebase,SesionFirebase}`, `firestore.rules`, `docs/*` | *(completar)* | Compilación contra las firmas públicas de Firestore y FirebaseUI 9.1.1; pruebas unitarias; pendiente la prueba en dispositivo con el proyecto de Firebase del equipo |
| 3 | 02/11/2026 | Claude (Anthropic) | Lab 6 para todos los roles: login único con FirebaseUI y rol desde Firestore; cliente (registro, hoteles, reservas con transacción, pago simulado, checkout, chat, taxi, perfil), admin de hotel (configuración, inventario, checkouts y cobro, chats, traslados, valoraciones, reportes), superadmin (usuarios, aprobación de taxistas, hoteles con invitación del administrador, reportes, auditoría, datos de demostración); reglas de seguridad de todas las colecciones y documentación | `login/**`, `cliente/**`, `adminhotel/**`, `superadmin/**`, `data/firebase/**`, `data/model/**`, `utils/**`, `res/layout/*`, `res/values/strings.xml`, `firestore.rules`, `README.md`, `PROJECT_CONTEXT.md`, `docs/*` | *(completar)* | Compilación de toda la app contra las firmas públicas de Firestore y FirebaseUI 9.1.1; pruebas unitarias (`ServicioTaxiTest`, `PerfilUsuarioTest`, `ReservaTest`); pendiente la prueba en dispositivo con el proyecto de Firebase del equipo y en el *Rules Playground* de la consola |

## Criterios de uso adoptados por el equipo

- La IA se usa como apoyo para andamiaje, redacción y revisión. **Las decisiones de
  diseño y arquitectura son del equipo** y están justificadas en
  [`arquitectura.md`](arquitectura.md).
- Todo código generado con asistencia de IA se **lee, se entiende y se prueba** antes de
  integrarse. No se integra código que algún integrante del equipo no pueda explicar.
- No se ingresan credenciales, claves de Firebase ni datos personales reales en los
  prompts.
- Los aportes generados con IA se identifican en el mensaje del commit cuando sean
  sustanciales.

## Anexo: registro de prompts

### Prompt 1 — 23/08/2026 — Andamiaje del repositorio

**Herramienta:** Claude (Anthropic)

**Contexto entregado:** descripción del proyecto RoomTrip (enunciado del curso, roles,
restricciones tecnológicas, arquitectura decidida y convenciones de trabajo).

**Solicitud:** pasos para crear el repositorio en GitHub y generación de la estructura
inicial de carpetas, `.gitignore`, `README.md` y plantillas de documentación.

**Resultado:** estructura del repositorio y archivos base de este commit inicial.

**Verificación realizada:** revisión manual de la estructura y del `.gitignore`,
confirmando que `google-services.json`, `local.properties`, keystores y `.env` quedan
excluidos del control de versiones.

### Prompt 2 — 02/11/2026 — Lab 6 (taxista): Firebase Authentication y Cloud Firestore

**Herramienta:** Claude (Anthropic)

**Contexto entregado:** el proyecto completo (zip), el plan de proyecto y las clases 9.1, 9.2 y 10
del curso (Firebase, Cloud Firestore y Firebase Authentication).

**Solicitud:** aplicar el entregable 6 (Firebase Authentication y Firebase Database), de ser posible
solo para el rol taxista.

**Resultado:** login del taxista con FirebaseUI (correo y Google) y verificación de correo;
validación de rol y aprobación en `usuarios/{uid}`; pedidos, aceptación (transacción), estados,
cierre por QR e historial sobre la colección `serviciosTaxi`; registro de eventos en `logs`; reglas
de seguridad (`firestore.rules`); pasos de configuración en el manual de instalación.

**Verificación realizada:** compilación del código del taxista y del login contra las firmas
públicas de Firestore (`api.txt`) y de FirebaseUI 9.1.1; pruebas unitarias (`ServicioTaxiTest`,
`PerfilTaxistaTest`). Pendiente: correr la app con el `google-services.json` del equipo.

### Prompt 3 — 02/11/2026 — Lab 6 (todos los roles)

**Herramienta:** Claude (Anthropic)

**Contexto entregado:** el mismo del prompt 2, con la versión del taxista ya terminada.

**Solicitud:** aplicar Firebase Authentication y Cloud Firestore a todos los roles, usando un único
proyecto de Firebase para todo el equipo.

**Decisiones acordadas con el equipo antes de implementar:** un solo login (FirebaseUI) y el rol
desde `usuarios/{uid}`; el cliente se registra con el formulario existente; el admin de hotel se da
de alta por invitación (`invitaciones/{correo}`) cuando el Superadmin registra el hotel; el
Superadmin se crea a mano en la consola; nuevo modelo `Reserva` con transacción contra fechas
superpuestas; chat en Firestore; fotos de hoteles de demostración incluidas en la app; botón para
cargar datos de demostración; se reutilizan los layouts del equipo.

**Resultado:** capa de datos común (`data/firebase/`), los cuatro módulos sobre Firestore, reglas
de seguridad de todas las colecciones (`firestore.rules`) y documentación actualizada (README,
PROJECT_CONTEXT, arquitectura, manuales).

**Verificación realizada:** compilación de toda la app contra las firmas públicas de Firestore
(`api.txt`) y de FirebaseUI 9.1.1; pruebas unitarias (`ServicioTaxiTest`, `PerfilUsuarioTest`,
`ReservaTest`). Pendiente: correr la app con el `google-services.json` del equipo y probar las
reglas en el *Rules Playground*.
