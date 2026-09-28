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
