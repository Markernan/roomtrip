# -*- coding: utf-8 -*-
"""Fuente de datos compartida de la matriz de requerimientos de RoomTrip.

La usan los dos generadores:
  generar_requerimientos_xlsx.py  -> versión interna del equipo
  generar_entrega_xlsx.py         -> versión que se entrega al profesor

Modelo de entregables
---------------------
Los laboratorios del curso NO reparten el alcance del sistema: son capas de
madurez sobre los mismos requerimientos. "Reservar con tarjeta" aparece en el
Lab 3 como pantalla navegable y en el Lab 6 cuando la reserva se persiste de
verdad. Por eso hay dos conceptos:

  Fases  -> laboratorios en los que se trabaja el requerimiento
  Cierre -> laboratorio en el que queda terminado (el último de sus fases)
"""
import datetime as dt
import pandas as pd

CSV = r"C:\Users\USUARIO\Desktop\roomtrip\docs\requerimientos.csv"

# ---------------------------------------------------------------- roles
ROL_DE_ACTOR = {
    "Superadmin": "Superadmin",
    "Admin de hotel": "Administrador de hotel",
    "Taxista": "Taxista",
    "Cliente": "Cliente",
    "Sistema": "Transversal",
    "Todos": "Transversal",
}
ORDEN_ROL = ["Superadmin", "Administrador de hotel", "Taxista", "Cliente", "Transversal"]

# ---------------------------------------------------------------- laboratorios
LABS = ["L3", "L4", "L5", "L6", "L7", "Web"]
LAB_NOMBRE = {"L3": "Lab 3", "L4": "Lab 4", "L5": "Lab 5",
              "L6": "Lab 6", "L7": "Lab 7", "Web": "App web"}
LAB_DESC = {
    "L3":  "Implementación de mockups: navigation, menús y elementos UI",
    "L4":  "RecyclerView: listado de elementos con data estática",
    "L5":  "Storage local y notificaciones",
    "L6":  "Firebase Authentication y Firebase Database",
    "L7":  "Presentación prefinal (85%)",
    "Web": "App web de taxistas y entrega final",
}
LAB_FECHA = {
    "L3": dt.date(2026, 9, 15),
    "L4": dt.date(2026, 9, 29),
    "L5": dt.date(2026, 10, 20),
    "L6": dt.date(2026, 11, 3),
    "L7": dt.date(2026, 12, 14),
    "Web": None,
}

FASES = {
    # --- Superadmin
    "RF-SA-01": ["L3", "L4", "L6"],
    "RF-SA-02": ["L3", "L4", "L6"],
    "RF-SA-03": ["Web"],
    "RF-SA-04": ["L3", "L4", "L6"],
    "RF-SA-05": ["L3", "L4", "L6"],
    # --- Administrador de hotel
    "RF-AH-01": ["L3", "L6", "L7"],
    "RF-AH-02": ["L3", "L4", "L5", "L6"],
    "RF-AH-03": ["L3", "L4", "L6"],
    "RF-AH-04": ["L3", "L4", "L6"],
    "RF-AH-05": ["L3", "L6", "L7"],
    "RF-AH-06": ["L3", "L5", "L6"],
    "RF-AH-07": ["L3", "L6"],
    "RF-AH-08": ["L3", "L4", "L6"],
    "RF-AH-09": ["L3", "L4", "L6"],
    "RF-AH-10": ["L3", "L6"],
    # --- Taxista
    "RF-TX-01": ["Web"],
    "RF-TX-02": ["L3", "L5", "L6"],
    "RF-TX-03": ["L3", "L4", "L5", "L6"],
    "RF-TX-04": ["L3", "L6"],
    "RF-TX-05": ["L6", "L7"],
    "RF-TX-06": ["L3", "L6"],
    "RF-TX-07": ["L3", "L7"],
    # --- Cliente
    "RF-CL-01": ["L3", "L5", "L6"],
    "RF-CL-02": ["L3", "L4", "L6"],
    "RF-CL-03": ["L3", "L6"],
    "RF-CL-04": ["L3", "L6"],
    "RF-CL-05": ["L3", "L5", "L6"],
    "RF-CL-06": ["L3", "L4", "L6"],
    "RF-CL-07": ["L3", "L4", "L6"],
    "RF-CL-08": ["L3", "L6", "L7"],
    "RF-CL-09": ["L3", "L7"],
    "RF-CL-10": ["L3", "L7"],
    "RF-CL-11": ["L3", "L6", "L7"],
    "RF-CL-12": ["L3", "L4", "L6"],
    # --- Transversales
    "RF-GN-01": ["L3", "L6"],
    "RF-GN-02": ["L3", "L6"],
    "RF-GN-03": ["L3", "L6"],
    "RF-GN-04": ["L3", "L4", "L6"],
    "RNF-01":   ["L6"],
    "RNF-02":   ["L6"],
    "RNF-03":   ["L6"],
    "RNF-04":   ["L6"],
    "RNF-05":   ["L7"],
    "RNF-06":   ["L7"],
    "RNF-07":   ["L3"],
    "RNF-08":   ["L6"],
    "RNF-09":   ["L7", "Web"],
}

# ---------------------------------------------------------------- requerimientos
# añadidos tras la revisión del 23/08/2026; el CSV se mantiene sin tocar.
NUEVOS = [
    {
        "ID": "RF-AH-10",
        "Módulo": "M8 Taxi",
        "Actor": "Admin de hotel",
        "Tipo": "Funcional",
        "Requerimiento": "Establecer el monto mínimo de reserva que da derecho al taxi "
                         "gratuito al aeropuerto",
        "Criterio de aceptación": "El hotel guarda un monto mínimo y solo las reservas que "
                                  "lo alcanzan habilitan la opción de taxi al cliente",
        "Prioridad": "Alta",
        "Estado": "Pendiente",
        "Mockup": "No",
    },
    {
        "ID": "RF-CL-12",
        "Módulo": "M3 Catálogo",
        "Actor": "Cliente",
        "Tipo": "Funcional",
        "Requerimiento": "Consultar los lugares históricos cercanos al hotel",
        "Criterio de aceptación": "El cliente ve la lista de lugares históricos cercanos "
                                  "registrados por el administrador del hotel",
        "Prioridad": "Media",
        "Estado": "Pendiente",
        "Mockup": "No",
    },
]

OBSERVACIONES = {
    "RF-AH-01": "Los lugares históricos cercanos aún no están en el mockup.",
    "RF-AH-02": "El Storage del Lab 5 es almacenamiento local; la subida a Firebase es del Lab 6.",
    "RF-AH-08": "El mockup existe pero el orden del reporte debe invertirse a menor → mayor.",
    "RF-AH-10": "Sin este monto no se puede evaluar la regla del taxi gratuito.",
    "RF-CL-01": "Faltan en el mockup el tipo de documento y la fecha de nacimiento.",
    "RF-CL-05": "Requiere Firebase Cloud Messaging.",
    "RF-CL-07": "Falta enganchar el chat al bottom navigation.",
    "RF-CL-09": "Necesita Google Maps SDK y la ubicación desde Realtime Database.",
    "RF-CL-12": "Falta la pantalla en Figma; el hotel ya los registra en RF-AH-01.",
    "RF-GN-01": "Validar con una transacción; no basta con comprobarlo en el cliente.",
    "RF-GN-02": "Validar con una transacción; no basta con comprobarlo en el cliente.",
    "RF-TX-04": "Requiere transacción para garantizar la exclusividad del pedido.",
    "RF-TX-05": "Se escribe en Realtime Database por la frecuencia de actualización.",
    "RF-TX-07": "Necesita librería de escaneo de QR.",
    "RNF-06": "Todo el manejo del fallo vive en TaxiRepository.",
}

ORDEN_CIERRE = [LAB_NOMBRE[l] for l in LABS]


def cargar():
    """Devuelve el DataFrame maestro ordenado por rol y por cierre."""
    df = pd.read_csv(CSV, sep=";", encoding="utf-8-sig").fillna("")
    df = df.drop(columns=[c for c in ("Entregable objetivo", "Depende de") if c in df.columns])
    df = pd.concat([df, pd.DataFrame(NUEVOS)], ignore_index=True).fillna("")

    df["Rol"] = df["Actor"].map(ROL_DE_ACTOR)
    df["Fases"] = df["ID"].map(lambda i: "  ·  ".join(FASES[i]))
    df["Cierre"] = df["ID"].map(lambda i: LAB_NOMBRE[FASES[i][-1]])
    df["Mockup"] = df["Mockup"].replace({"Corregir orden": "Parcial", "": "NA"})
    df["Observaciones"] = df["ID"].map(OBSERVACIONES).fillna("")
    df["Responsable"] = ""

    df["_rol"] = df["Rol"].map(ORDEN_ROL.index)
    df["_cie"] = df["Cierre"].map(ORDEN_CIERRE.index)
    df["_pri"] = df["Prioridad"].map({"Alta": 0, "Media": 1, "Baja": 2})
    return df.sort_values(["_rol", "_cie", "_pri", "ID"]).reset_index(drop=True)


def modulos(df):
    return sorted(df["Módulo"].unique(), key=lambda m: int(m.split()[0][1:]))


def plural(n, singular="requerimiento"):
    return singular if n == 1 else singular + "s"
