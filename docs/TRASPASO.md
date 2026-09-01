# Traspaso de contexto — RoomTrip

> Documento para retomar el trabajo desde otra máquina (Claude Code de la laptop)
> sin perder el hilo. Escrito el 23/08/2026.
> Fecha del sistema en la sesión de trabajo: 01/09/2026 (día de la entrega 2).

---

## 1. Qué es este proyecto

**RoomTrip** — app de reservas de alojamiento en hoteles. Curso **1TEL05 Servicios y
Aplicaciones para IoT**, PUCP, semestre 2026-2, **Laboratorio 0891, Grupo 3**.

- **Líder:** Mark Hernán Valencia Quiroz (el usuario). Correo: tamoactivoite@gmail.com / a20221747@pucp.edu.pe
- **Equipo:** Eduardo Rodas, Eva Maria Mauricio, Santiago Linares, Willian Antaurco.
- **Coordinador/cliente:** Oscar Díaz.

Todo el contexto del sistema (arquitectura, roles, reglas de negocio, restricciones,
calendario) está en el **CLAUDE.md** del proyecto y en `README.md`. **Leerlos primero.**

Puntos que NO se deben olvidar:
- App móvil **Android nativa en Java**, mínimo **API 34**. No Kotlin, no Flutter.
- Persistencia NoSQL (Firebase/Firestore + Realtime Database).
- App web de taxistas separada, con su propia BD (MongoDB) y API REST.
- La app móvil **nunca** accede directo a la BD de taxistas: solo por API REST.
- Marca **RoomTrip**, paleta turquesa, mockups en Figma.

---

## 2. Preferencias del usuario (IMPORTANTE)

1. **NUNCA añadir el trailer `Co-Authored-By: Claude`** en los commits. Ya causó que
   Claude apareciera en los contribuidores de GitHub y hubo que reescribir el historial.
   Los commits van solo a nombre del usuario (`Markernan <a20221747@pucp.edu.pe>`).
2. El uso de IA se declara en `docs/declaracion-ia.md`, no en git.
3. El usuario prefiere **entregar al profesor solo lo que el enunciado pide**, sin
   requerimientos "de más" que puedan delatar huecos o sobre-comprometer al equipo.
4. Responder siempre en **español**.

---

## 3. Estado del repositorio

- Ubicación local en la PC de escritorio: `C:\Users\USUARIO\Desktop\roomtrip`
- Remoto: `https://github.com/Markernan/roomtrip.git`
- Rama: `main`

### ⚠️ Pendientes de git al momento de escribir esto

- **Hay 7 commits locales SIN subir** (`main ahead 7`). Falta hacer `git push`.
- Se colaron archivos `docs/__pycache__/*.pyc` al control de versiones. Hay que
  quitarlos del índice y añadir `__pycache__/` al `.gitignore`.

```bash
cd ~/Desktop/roomtrip   # (o la ruta equivalente en la laptop)
git rm -r --cached docs/__pycache__
echo "__pycache__/" >> .gitignore
echo "*.pyc" >> .gitignore
git add .gitignore
git commit -m "chore: deja de versionar __pycache__"
git push
```

---

## 4. Lo que ya está hecho: la matriz de requerimientos

Esta es la parte central del trabajo hasta ahora. **Entregable 2** = lista de
requerimientos + 100% de mockups (los mockups los maneja el equipo en Figma, aparte).

### Arquitectura de los archivos (todo en `docs/`)

La matriz NO se edita a mano. Se genera con scripts Python desde una fuente única:

| Archivo | Qué es |
|---|---|
| `requerimientos_datos.py` | **Fuente de datos única.** Aquí viven: los requerimientos nuevos (`NUEVOS`), las fases por laboratorio (`FASES`), las observaciones, los derivados que no van al profesor (`SOLO_INTERNOS`) y las precisiones de criterio (`CRITERIOS`). **Editar aquí.** |
| `requerimientos_estilo.py` | Estilos y helpers de openpyxl compartidos (colores turquesa, cabeceras, formato condicional). |
| `generar_requerimientos_xlsx.py` | Genera la **versión interna** del equipo → `requerimientos.xlsx` |
| `generar_entrega_xlsx.py` | Genera la **versión para el profesor** → `requerimientos-entrega.xlsx` |
| `requerimientos.csv` | Base histórica de 45 requerimientos (separador `;`, UTF-8 con BOM). Los 2 derivados se añaden en código, no aquí. El usuario dijo que NO lo usa; se mantiene solo como fuente de textos. |
| `requerimientos.md` | Versión en markdown de la lista (documentación). |

### Cómo regenerar los Excel

```bash
cd docs
python generar_requerimientos_xlsx.py    # -> requerimientos.xlsx (interno)
python generar_entrega_xlsx.py           # -> requerimientos-entrega.xlsx (profesor)
```

> Si el archivo está abierto en Excel, `python generar_*.py <ruta_temporal.xlsx>`
> genera en otra ruta y luego se copia.

### Verificar fórmulas (Windows, sin LibreOffice)

El `recalc.py` de la skill xlsx NO corre en Windows (falla por `socket.AF_UNIX`).
Se verifica con Excel vía COM en PowerShell (enlace tardío con `InvokeMember`, ProgID
`Excel.Application`, `CalculateFullRebuild`, `SpecialCells(-4123, 16)` para errores).
En la última corrida: interno 1261 fórmulas, entrega 90, **cero errores** en ambos.

### Contenido de cada versión

**Interna (`requerimientos.xlsx`)** — 47 requerimientos, 5 hojas:
Guía, Resumen, Requerimientos (editable, con Estado/Mockup/Responsable/Observaciones/
Fases/Cierre), Por rol, Plan por laboratorio.

**Entrega (`requerimientos-entrega.xlsx`)** — 45 requerimientos, 4 hojas:
Portada, Resumen, Requerimientos, Por rol. Solo columnas
`ID · Módulo · Rol · Tipo · Requerimiento · Criterio de aceptación · Prioridad`.
**Sin** estado, mockups, responsables ni plan por laboratorio.

### Diferencia 47 vs 45

Los 45 son 1-a-1 con el enunciado. Los 2 extra (`SOLO_INTERNOS`) son derivados que el
equipo necesita pero el enunciado no enuncia literalmente, así que se ocultan al profesor:
- **RF-AH-10** — el hotel establece el monto mínimo que da derecho al taxi gratis.
- **RF-CL-12** — el cliente consulta los lugares históricos cercanos.

### Modelo de "Fases" y "Cierre" (solo interno)

Los laboratorios NO reparten el alcance: son capas de madurez sobre los mismos
requerimientos. "Fases" = labs en los que se trabaja un requerimiento; "Cierre" = lab
en el que queda terminado. La suma de "se trabaja" es mayor que 45 a propósito.
Carga por lab (cierres): L3→1, L4→0, L5→0, **L6→33**, L7→10, Web→3. El Lab 6 (03/11)
es el cuello de botella: casi todo cierra ahí (Firebase).

---

## 5. ⚠️ Antes de enviar la versión al profesor

1. **Completar la celda amarilla "Integrantes"** en la hoja Portada de
   `requerimientos-entrega.xlsx`. Es el único campo vacío.
2. Está pendiente decidir en equipo qué documento es el oficial (ver sección 6).

---

## 6. Asunto abierto: la ERS de otro compañero (158 requisitos)

Un compañero armó por su cuenta una **ERS formal (IEEE 830 / ISO 29148)** con
**119 RF + 39 RNF = 158 requisitos**, frente a los 45-47 de nuestra matriz. Se revisó
y comparó con el enunciado. Conclusiones que hay que llevar a la reunión de equipo:

**Problema de forma que hay que corregir:** el documento llama al sistema **"HotelStay"**
en portada, alcance y pie de página. Debe decir **RoomTrip**.

**Riesgo principal:** dos documentos con conteos tan distintos (45 vs 158) pueden leerse
como falta de coordinación del equipo. **Hay que decidir cuál es el oficial.**

**De los 158, la mayoría (~93) no es scope extra**, solo la misma cosa partida en
oraciones más finas (estilo IEEE). Pero ~20 SÍ añaden trabajo/riesgo no pedido:

*Alto riesgo (recomendado bajar a Baja o quitar):*
- RF-RES-012 — cancelar reserva (el enunciado NO menciona cancelación)
- RF-REP-001/002/003 — reportes de ventas diario/mensual/anual (no pedidos; el
  enunciado solo pide reporte de servicios adicionales y de reservas por hotel)
- RNF-RND-001/002/003 — SLAs numéricos (listado 3s, chat 2s, ubicación 15s) — medibles
  y no pedidos
- RNF-USA-004 — "máximo 3 toques" (regla UX inventada)
- RF-CAT-002 — buscador con filtros (el enunciado dice "consultar", no "buscar/filtrar")

*Inconsistencia interna:* RF-USR-008 pone la aprobación de taxistas en la app **móvil**;
el enunciado dice que va en la app **web** (y ya está bien en RF-WTX-004). Es duplicado
mal ubicado.

*Lo mejor de esa ERS (no tocar):* MOD-TAX (18 req. de taxi) y MOD-API están muy bien.
Y confirmó de forma independiente los mismos 2 huecos que detectamos (monto mínimo del
taxi y consulta de lugares históricos), buena señal de que son reales.

---

## 7. Siguientes pasos sugeridos

1. Subir los commits pendientes y limpiar `__pycache__` (sección 3).
2. Completar "Integrantes" y enviar `requerimientos-entrega.xlsx` al profesor; enviar
   `requerimientos.xlsx` a los compañeros.
3. Reunión de equipo para decidir: ¿matriz de 45 o ERS de 158 como documento oficial?
   Si es la ERS, corregir "HotelStay"→"RoomTrip" y podar los ~20 de alto riesgo.
4. Foco real de la semana: los mockups en Figma (Superadmin 0%, Taxista ~5%).
5. Más adelante (no urgente): generar el proyecto Android en `android-app/` desde
   Android Studio (Empty Views Activity, Java, minSdk 34), y decidir el stack de
   `taxi-service/` (Node/Express vs Spring Boot).

---

## 8. Notas técnicas del entorno

- SO: Windows 10. Shell principal PowerShell; también hay Bash (Git Bash).
- Python 3.14, con `openpyxl` y `pandas` instalados.
- Excel instalado (Office16). LibreOffice NO está instalado.
- `gh` (GitHub CLI) NO estaba instalado en la PC de escritorio; el push se hizo por
  git normal con remoto HTTPS ya configurado.
- Git configurado como `Markernan <a20221747@pucp.edu.pe>`.
