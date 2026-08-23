# -*- coding: utf-8 -*-
"""Genera docs/requerimientos.xlsx de RoomTrip a partir de docs/requerimientos.csv.

Modelo de entregables
---------------------
Los laboratorios del curso NO reparten el alcance del sistema: son capas de
madurez sobre los mismos requerimientos. Un requerimiento como "reservar con
tarjeta" aparece en el Lab 3 como pantalla navegable y en el Lab 6 cuando la
reserva se persiste de verdad. Por eso se usan dos columnas:

  Fases  -> en qué laboratorios se trabaja el requerimiento (L3 · L4 · L6)
  Cierre -> el laboratorio en el que queda terminado
"""
import datetime as dt
import pandas as pd
from openpyxl import Workbook
from openpyxl.styles import Font, PatternFill, Alignment, Border, Side
from openpyxl.utils import get_column_letter
from openpyxl.worksheet.datavalidation import DataValidation
from openpyxl.formatting.rule import CellIsRule, DataBarRule
from openpyxl.chart import BarChart, Reference

import sys

CSV = r"C:\Users\USUARIO\Desktop\roomtrip\docs\requerimientos.csv"
OUT = r"C:\Users\USUARIO\Desktop\roomtrip\docs\requerimientos.xlsx"
if len(sys.argv) > 1:          # permite generar en otra ruta si el libro está abierto
    OUT = sys.argv[1]

# ---------------------------------------------------------------- paleta
TURQ_OSC = "00696E"
TURQ = "00838F"
TURQ_MED = "4DB6AC"
TURQ_CLA = "B2DFDB"
TURQ_PALE = "E0F2F1"
GRIS_TXT = "37474F"
BLANCO = "FFFFFF"
VERDE_F, VERDE_T = "C8E6C9", "1B5E20"
AMBAR_F, AMBAR_T = "FFE0B2", "E65100"
ROJO_F, ROJO_T = "FFCDD2", "B71C1C"
GRIS_F, GRIS_T = "ECEFF1", "546E7A"

FUENTE = "Arial"
thin = Side(style="thin", color="CFD8DC")
BORDE = Border(left=thin, right=thin, top=thin, bottom=thin)

def F(size=10, bold=False, color=GRIS_TXT, italic=False):
    return Font(name=FUENTE, size=size, bold=bold, color=color, italic=italic)

# ---------------------------------------------------------------- laboratorios
LABS = ["L3", "L4", "L5", "L6", "L7", "Web"]
LAB_NOMBRE = {
    "L3":  "Lab 3",
    "L4":  "Lab 4",
    "L5":  "Lab 5",
    "L6":  "Lab 6",
    "L7":  "Lab 7",
    "Web": "App web",
}
LAB_DESC = {
    "L3":  "Implementación de mockups: navigation, menús y elementos UI",
    "L4":  "RecyclerView: listado de elementos con data estática",
    "L5":  "Storage local y notificaciones",
    "L6":  "Firebase Authentication y Firebase Database",
    "L7":  "Presentación prefinal (85%)",
    "Web": "App web de taxistas y entrega final",
}
LAB_FECHA = {
    "L3":  dt.date(2026, 9, 15),
    "L4":  dt.date(2026, 9, 29),
    "L5":  dt.date(2026, 10, 20),
    "L6":  dt.date(2026, 11, 3),
    "L7":  dt.date(2026, 12, 14),
    "Web": None,
}

# Fases de cada requerimiento. El último elemento define el cierre.
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
    "RF-CL-05": ["L5", "L6"],
    "RF-CL-06": ["L3", "L4", "L6"],
    "RF-CL-07": ["L3", "L4", "L6"],
    "RF-CL-08": ["L3", "L6", "L7"],
    "RF-CL-09": ["L3", "L7"],
    "RF-CL-10": ["L3", "L7"],
    "RF-CL-11": ["L3", "L6", "L7"],
    # --- Transversales
    "RF-GN-01": ["L6"],
    "RF-GN-02": ["L6"],
    "RF-GN-03": ["L3", "L6"],
    "RF-GN-04": ["L6"],
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

# ---------------------------------------------------------------- datos
df = pd.read_csv(CSV, sep=";", encoding="utf-8-sig").fillna("")

ROL_DE_ACTOR = {
    "Superadmin": "Superadmin",
    "Admin de hotel": "Administrador de hotel",
    "Taxista": "Taxista",
    "Cliente": "Cliente",
    "Sistema": "Transversal",
    "Todos": "Transversal",
}
ORDEN_ROL = ["Superadmin", "Administrador de hotel", "Taxista", "Cliente", "Transversal"]

OBSERVACIONES = {
    "RF-AH-08": "El mockup existe pero el orden del reporte debe invertirse a menor → mayor.",
    "RF-CL-01": "Faltan en el mockup el tipo de documento y la fecha de nacimiento.",
    "RF-CL-07": "Falta enganchar el chat al bottom navigation.",
    "RF-AH-01": "Los lugares históricos cercanos aún no están en el mockup.",
    "RF-AH-02": "El Storage del Lab 5 es almacenamiento local; la subida a Firebase es del Lab 6.",
    "RF-CL-05": "Requiere Firebase Cloud Messaging.",
    "RF-TX-07": "Necesita librería de escaneo de QR.",
    "RF-CL-09": "Necesita Google Maps SDK y la ubicación desde Realtime Database.",
    "RF-GN-01": "Validar con una transacción; no basta con comprobarlo en el cliente.",
    "RF-GN-02": "Validar con una transacción; no basta con comprobarlo en el cliente.",
    "RF-TX-04": "Requiere transacción para garantizar la exclusividad del pedido.",
    "RF-TX-05": "Se escribe en Realtime Database por la frecuencia de actualización.",
    "RNF-06": "Todo el manejo del fallo vive en TaxiRepository.",
}

df["Rol"] = df["Actor"].map(ROL_DE_ACTOR)
df["Fases"] = df["ID"].map(lambda i: "  ·  ".join(FASES[i]))
df["Cierre"] = df["ID"].map(lambda i: LAB_NOMBRE[FASES[i][-1]])
df["Mockup"] = df["Mockup"].replace({"Corregir orden": "Parcial", "": "NA"})
df["Observaciones"] = df["ID"].map(OBSERVACIONES).fillna("")
df["Responsable"] = ""

ORDEN_CIERRE = [LAB_NOMBRE[l] for l in LABS]
df["_rol"] = df["Rol"].map(ORDEN_ROL.index)
df["_cie"] = df["Cierre"].map(ORDEN_CIERRE.index)
df["_pri"] = df["Prioridad"].map({"Alta": 0, "Media": 1, "Baja": 2})
maestra = df.sort_values(["_rol", "_cie", "_pri", "ID"]).reset_index(drop=True)

wb = Workbook()

# ================================================================ helpers
def titulo_hoja(ws, texto, subtitulo, ncols, nota=None):
    ws.merge_cells(start_row=1, start_column=1, end_row=1, end_column=ncols)
    c = ws.cell(row=1, column=1, value=texto)
    c.font = Font(name=FUENTE, size=16, bold=True, color=BLANCO)
    c.alignment = Alignment(horizontal="left", vertical="center", indent=1)
    ws.row_dimensions[1].height = 34
    ws.merge_cells(start_row=2, start_column=1, end_row=2, end_column=ncols)
    c = ws.cell(row=2, column=1, value=subtitulo)
    c.font = Font(name=FUENTE, size=9, color=BLANCO)
    c.alignment = Alignment(horizontal="left", vertical="center", indent=1)
    ws.row_dimensions[2].height = 18
    for col in range(1, ncols + 1):
        ws.cell(row=1, column=col).fill = PatternFill("solid", fgColor=TURQ)
        ws.cell(row=2, column=col).fill = PatternFill("solid", fgColor=TURQ_MED)
    if nota:
        ws.merge_cells(start_row=3, start_column=1, end_row=3, end_column=ncols)
        c = ws.cell(row=3, column=1, value=nota)
        c.font = Font(name=FUENTE, size=9, italic=True, color=TURQ_OSC)
        c.alignment = Alignment(horizontal="left", vertical="center", indent=1)
        ws.row_dimensions[3].height = 16

def encabezado(ws, fila, headers, anchos):
    for i, (h, w) in enumerate(zip(headers, anchos), start=1):
        c = ws.cell(row=fila, column=i, value=h)
        c.font = Font(name=FUENTE, size=10, bold=True, color=BLANCO)
        c.fill = PatternFill("solid", fgColor=TURQ_OSC)
        c.alignment = Alignment(horizontal="center", vertical="center", wrap_text=True)
        c.border = BORDE
        ws.column_dimensions[get_column_letter(i)].width = w
    ws.row_dimensions[fila].height = 30

def banda(ws, fila, texto, ncols, fill=TURQ_CLA, color=TURQ_OSC, size=11):
    ws.merge_cells(start_row=fila, start_column=1, end_row=fila, end_column=ncols)
    for col in range(1, ncols + 1):
        cc = ws.cell(row=fila, column=col)
        cc.fill = PatternFill("solid", fgColor=fill)
        cc.border = BORDE
    c = ws.cell(row=fila, column=1, value=texto)
    c.font = Font(name=FUENTE, size=size, bold=True, color=color)
    c.alignment = Alignment(horizontal="left", vertical="center", indent=1)
    ws.row_dimensions[fila].height = 22

def reglas_estado(ws, rango):
    for val, fill, txt, bold in (("Hecho", VERDE_F, VERDE_T, True),
                                 ("En progreso", AMBAR_F, AMBAR_T, True),
                                 ("Bloqueado", ROJO_F, ROJO_T, True),
                                 ("Pendiente", GRIS_F, GRIS_T, False)):
        ws.conditional_formatting.add(rango, CellIsRule(
            operator="equal", formula=[f'"{val}"'],
            fill=PatternFill("solid", fgColor=fill),
            font=Font(name=FUENTE, size=10, bold=bold, color=txt)))

def reglas_mockup(ws, rango):
    for val, fill, txt, bold in (("Sí", VERDE_F, VERDE_T, False),
                                 ("Parcial", AMBAR_F, AMBAR_T, False),
                                 ("No", ROJO_F, ROJO_T, True)):
        ws.conditional_formatting.add(rango, CellIsRule(
            operator="equal", formula=[f'"{val}"'],
            fill=PatternFill("solid", fgColor=fill),
            font=Font(name=FUENTE, size=10, bold=bold, color=txt)))

def reglas_prioridad(ws, rango):
    ws.conditional_formatting.add(rango, CellIsRule(operator="equal", formula=['"Alta"'],
        font=Font(name=FUENTE, size=10, bold=True, color=ROJO_T)))
    ws.conditional_formatting.add(rango, CellIsRule(operator="equal", formula=['"Media"'],
        font=Font(name=FUENTE, size=10, color=AMBAR_T)))

# ================================================================ 1. Requerimientos
ws = wb.active
ws.title = "Requerimientos"
NC = 13
HDR = ["ID", "Módulo", "Rol", "Tipo", "Requerimiento", "Criterio de aceptación",
       "Prioridad", "Fases", "Cierre", "Estado", "Mockup", "Responsable", "Observaciones"]
ANCHOS = [11, 22, 21, 12, 52, 58, 10, 20, 12, 13, 10, 16, 46]
titulo_hoja(ws, "RoomTrip · Matriz de requerimientos",
            "1TEL05 Servicios y Aplicaciones para IoT · PUCP · Semestre 2026-2", NC,
            "Única hoja editable. «Fases» son los laboratorios en los que se trabaja el "
            "requerimiento; «Cierre» es aquel en el que queda terminado.")
FILA_HDR = 5
encabezado(ws, FILA_HDR, HDR, ANCHOS)

COLS = ["ID", "Módulo", "Rol", "Tipo", "Requerimiento", "Criterio de aceptación",
        "Prioridad", "Fases", "Cierre", "Estado", "Mockup", "Responsable", "Observaciones"]
r = FILA_HDR + 1
for _, row in maestra.iterrows():
    for i, col in enumerate(COLS, start=1):
        c = ws.cell(row=r, column=i, value=row[col])
        c.font = F()
        c.border = BORDE
        if i in (1, 3, 4, 7, 8, 9, 10, 11):
            c.alignment = Alignment(horizontal="center", vertical="center", wrap_text=True)
        else:
            c.alignment = Alignment(horizontal="left", vertical="top", wrap_text=True)
    ws.cell(row=r, column=1).font = F(bold=True, color=TURQ_OSC)
    ws.cell(row=r, column=8).font = F(color=TURQ_OSC)
    ws.row_dimensions[r].height = 42
    r += 1
ULT = r - 1

ws.auto_filter.ref = f"A{FILA_HDR}:M{ULT}"
ws.freeze_panes = "B6"
reglas_estado(ws, f"J6:J{ULT}")
reglas_mockup(ws, f"K6:K{ULT}")
reglas_prioridad(ws, f"G6:G{ULT}")

for formula, rng in (('"Pendiente,En progreso,Hecho,Bloqueado"', f"J6:J{ULT}"),
                     ('"Alta,Media,Baja"', f"G6:G{ULT}"),
                     ('"Sí,Parcial,No,NA"', f"K6:K{ULT}")):
    dv = DataValidation(type="list", formula1=formula, allow_blank=False)
    ws.add_data_validation(dv)
    dv.add(rng)

ws.sheet_view.showGridLines = False
ws.print_title_rows = f"{FILA_HDR}:{FILA_HDR}"
ws.page_setup.orientation = "landscape"
ws.sheet_properties.pageSetUpPr.fitToPage = True
ws.page_setup.fitToWidth = 1

# ================================================================ 2. Resumen
rs = wb.create_sheet("Resumen", 0)
NCR = 9
titulo_hoja(rs, "RoomTrip · Resumen de requerimientos",
            "Todos los valores se calculan con fórmulas sobre la hoja Requerimientos", NCR,
            "Se actualiza solo al cambiar el estado de un requerimiento.")
for i, w in enumerate([28, 13, 13, 13, 14, 13, 13, 14, 14], start=1):
    rs.column_dimensions[get_column_letter(i)].width = w

R = f"Requerimientos!$A$6:$A${ULT}"
ROL_R = f"Requerimientos!$C$6:$C${ULT}"
TIPO_R = f"Requerimientos!$D$6:$D${ULT}"
PRI_R = f"Requerimientos!$G$6:$G${ULT}"
FAS_R = f"Requerimientos!$H$6:$H${ULT}"
CIE_R = f"Requerimientos!$I$6:$I${ULT}"
EST_R = f"Requerimientos!$J$6:$J${ULT}"
MOC_R = f"Requerimientos!$K$6:$K${ULT}"
MOD_R = f"Requerimientos!$B$6:$B${ULT}"

def kpi(fila, col, etiqueta, formula, fmt="0", destaque=False):
    c = rs.cell(row=fila, column=col, value=etiqueta)
    c.font = F(9, color=TURQ_OSC)
    c.alignment = Alignment(horizontal="center", vertical="center", wrap_text=True)
    c.fill = PatternFill("solid", fgColor=TURQ_PALE)
    c.border = BORDE
    v = rs.cell(row=fila + 1, column=col, value=formula)
    v.font = Font(name=FUENTE, size=18, bold=True, color=(ROJO_T if destaque else TURQ))
    v.alignment = Alignment(horizontal="center", vertical="center")
    v.fill = PatternFill("solid", fgColor=TURQ_PALE)
    v.border = BORDE
    v.number_format = fmt

banda(rs, 5, "Indicadores generales", NCR)
rs.row_dimensions[6].height = 30
rs.row_dimensions[7].height = 34
kpi(6, 1, "Total de requerimientos", f"=COUNTA({R})")
kpi(6, 2, "Funcionales", f'=COUNTIF({TIPO_R},"Funcional")')
kpi(6, 3, "No funcionales", f'=COUNTIF({TIPO_R},"No funcional")')
kpi(6, 4, "Prioridad alta", f'=COUNTIF({PRI_R},"Alta")')
kpi(6, 5, "Hechos", f'=COUNTIF({EST_R},"Hecho")')
kpi(6, 6, "En progreso", f'=COUNTIF({EST_R},"En progreso")')
kpi(6, 7, "Pendientes", f'=COUNTIF({EST_R},"Pendiente")')
kpi(6, 8, "% de avance", f'=IFERROR(COUNTIF({EST_R},"Hecho")/COUNTA({R}),0)', "0.0%")
kpi(6, 9, "Mockups faltantes", f'=COUNTIF({MOC_R},"No")', "0", destaque=True)

# --- por rol
banda(rs, 9, "Requerimientos por rol", NCR)
encabezado(rs, 10, ["Rol", "Total", "Alta", "Media", "Pendiente", "En progreso",
                    "Hecho", "% avance", "Sin mockup"],
           [28, 13, 13, 13, 14, 13, 13, 14, 14])
f0 = 11
for i, rol in enumerate(ORDEN_ROL):
    f = f0 + i
    rs.cell(row=f, column=1, value=rol).font = F(bold=True)
    rs.cell(row=f, column=2, value=f"=COUNTIF({ROL_R},$A{f})")
    rs.cell(row=f, column=3, value=f'=COUNTIFS({ROL_R},$A{f},{PRI_R},"Alta")')
    rs.cell(row=f, column=4, value=f'=COUNTIFS({ROL_R},$A{f},{PRI_R},"Media")')
    rs.cell(row=f, column=5, value=f'=COUNTIFS({ROL_R},$A{f},{EST_R},"Pendiente")')
    rs.cell(row=f, column=6, value=f'=COUNTIFS({ROL_R},$A{f},{EST_R},"En progreso")')
    rs.cell(row=f, column=7, value=f'=COUNTIFS({ROL_R},$A{f},{EST_R},"Hecho")')
    rs.cell(row=f, column=8, value=f"=IFERROR($G{f}/$B{f},0)")
    rs.cell(row=f, column=9, value=f'=COUNTIFS({ROL_R},$A{f},{MOC_R},"No")')
    for col in range(1, NCR + 1):
        c = rs.cell(row=f, column=col)
        c.border = BORDE
        if col > 1:
            c.font = F()
            c.alignment = Alignment(horizontal="center", vertical="center")
        if col == 8:
            c.number_format = "0.0%"
        if f % 2 == 0:
            c.fill = PatternFill("solid", fgColor="F7FBFB")
f_tot = f0 + len(ORDEN_ROL)
rs.cell(row=f_tot, column=1, value="Total")
for col in range(2, 8):
    L = get_column_letter(col)
    rs.cell(row=f_tot, column=col, value=f"=SUM({L}{f0}:{L}{f_tot-1})")
rs.cell(row=f_tot, column=8, value=f"=IFERROR($G{f_tot}/$B{f_tot},0)")
rs.cell(row=f_tot, column=9, value=f"=SUM(I{f0}:I{f_tot-1})")
for col in range(1, NCR + 1):
    c = rs.cell(row=f_tot, column=col)
    c.font = Font(name=FUENTE, size=10, bold=True, color=BLANCO)
    c.fill = PatternFill("solid", fgColor=TURQ_MED)
    c.border = BORDE
    if col > 1:
        c.alignment = Alignment(horizontal="center", vertical="center")
    if col == 8:
        c.number_format = "0.0%"
rs.conditional_formatting.add(f"H{f0}:H{f_tot-1}",
    DataBarRule(start_type="num", start_value=0, end_type="num", end_value=1, color=TURQ_MED))

# --- por laboratorio
f1 = f_tot + 2
banda(rs, f1, "Carga por laboratorio", NCR)
rs.merge_cells(start_row=f1 + 1, start_column=1, end_row=f1 + 1, end_column=NCR)
c = rs.cell(row=f1 + 1, column=1,
            value="«Se trabaja» cuenta los requerimientos que se tocan en ese laboratorio; "
                  "«Cierra aquí», los que quedan terminados. Un requerimiento se trabaja en varios.")
c.font = F(9, italic=True, color=GRIS_T)
c.alignment = Alignment(horizontal="left", vertical="center", indent=1)
encabezado(rs, f1 + 2, ["Laboratorio", "Fecha", "Días restantes", "Se trabaja",
                        "Cierra aquí", "Alta", "Pendiente", "% avance", "Sin mockup"],
           [28, 13, 13, 13, 14, 13, 13, 14, 14])
f2 = f1 + 3
for i, lab in enumerate(LABS):
    f = f2 + i
    nombre = LAB_NOMBRE[lab]
    rs.cell(row=f, column=1, value=nombre).font = F(bold=True)
    fecha = LAB_FECHA[lab]
    if fecha:
        cf = rs.cell(row=f, column=2, value=fecha)
        cf.number_format = "DD/MM/YYYY"
        rs.cell(row=f, column=3, value=f"=$B{f}-TODAY()")
    else:
        rs.cell(row=f, column=2, value="Por definir")
        rs.cell(row=f, column=3, value="—")
    rs.cell(row=f, column=4, value=f'=COUNTIF({FAS_R},"*{lab}*")')
    rs.cell(row=f, column=5, value=f"=COUNTIF({CIE_R},$A{f})")
    rs.cell(row=f, column=6, value=f'=COUNTIFS({CIE_R},$A{f},{PRI_R},"Alta")')
    rs.cell(row=f, column=7, value=f'=COUNTIFS({CIE_R},$A{f},{EST_R},"Pendiente")')
    rs.cell(row=f, column=8, value=f'=IFERROR(COUNTIFS({CIE_R},$A{f},{EST_R},"Hecho")/$E{f},0)')
    rs.cell(row=f, column=9, value=f'=COUNTIFS({CIE_R},$A{f},{MOC_R},"No")')
    for col in range(1, NCR + 1):
        c = rs.cell(row=f, column=col)
        c.border = BORDE
        if col > 1:
            c.font = F()
            c.alignment = Alignment(horizontal="center", vertical="center")
        if col == 8:
            c.number_format = "0.0%"
        if f % 2 == 0:
            c.fill = PatternFill("solid", fgColor="F7FBFB")
rs.conditional_formatting.add(f"H{f2}:H{f2+len(LABS)-1}",
    DataBarRule(start_type="num", start_value=0, end_type="num", end_value=1, color=TURQ_MED))

# --- por modulo
f3 = f2 + len(LABS) + 1
banda(rs, f3, "Requerimientos por módulo funcional", NCR)
encabezado(rs, f3 + 1, ["Módulo", "Total", "Alta", "Pendiente", "Hecho", "% avance", "", "", ""],
           [28, 13, 13, 14, 13, 14, 13, 14, 14])
modulos = sorted(maestra["Módulo"].unique(), key=lambda m: int(m.split()[0][1:]))
f4 = f3 + 2
for i, mod in enumerate(modulos):
    f = f4 + i
    rs.cell(row=f, column=1, value=mod).font = F()
    rs.cell(row=f, column=2, value=f"=COUNTIF({MOD_R},$A{f})")
    rs.cell(row=f, column=3, value=f'=COUNTIFS({MOD_R},$A{f},{PRI_R},"Alta")')
    rs.cell(row=f, column=4, value=f'=COUNTIFS({MOD_R},$A{f},{EST_R},"Pendiente")')
    rs.cell(row=f, column=5, value=f'=COUNTIFS({MOD_R},$A{f},{EST_R},"Hecho")')
    rs.cell(row=f, column=6, value=f"=IFERROR($E{f}/$B{f},0)")
    for col in range(1, 7):
        c = rs.cell(row=f, column=col)
        c.border = BORDE
        if col > 1:
            c.font = F()
            c.alignment = Alignment(horizontal="center", vertical="center")
        if col == 6:
            c.number_format = "0.0%"
        if f % 2 == 0:
            c.fill = PatternFill("solid", fgColor="F7FBFB")
f5 = f4 + len(modulos)

ch = BarChart()
ch.type = "col"
ch.style = 2
ch.title = "Requerimientos por rol"
ch.y_axis.title = "Cantidad"
ch.height, ch.width = 7.5, 16
ch.add_data(Reference(rs, min_col=2, min_row=10, max_row=f_tot - 1), titles_from_data=True)
ch.set_categories(Reference(rs, min_col=1, min_row=f0, max_row=f_tot - 1))
ch.legend = None
rs.add_chart(ch, f"A{f5 + 2}")

rs.sheet_view.showGridLines = False
rs.freeze_panes = "A5"

# ================================================================ 3. Por rol
pr = wb.create_sheet("Por rol", 2)
NCP = 8
titulo_hoja(pr, "RoomTrip · Requerimientos agrupados por rol",
            "Vista de lectura. El contenido se trae por fórmula desde la hoja Requerimientos", NCP,
            "Para modificar un requerimiento, edítalo en la hoja «Requerimientos».")
encabezado(pr, 5, ["ID", "Requerimiento", "Módulo", "Prioridad", "Cierre",
                   "Estado", "Mockup", "Responsable"],
           [11, 66, 22, 11, 12, 13, 10, 18])
MAP_P = [("Requerimiento", "E"), ("Módulo", "B"), ("Prioridad", "G"),
         ("Cierre", "I"), ("Estado", "J"), ("Mockup", "K"), ("Responsable", "L")]

def vista(hoja, grupos, mapeo, ncols):
    f = 6
    rangos = []
    for etiqueta, sub in grupos:
        banda(hoja, f, etiqueta, ncols)
        ini = f + 1
        f += 1
        for _, row in sub.iterrows():
            c = hoja.cell(row=f, column=1, value=row["ID"])
            c.font = F(bold=True, color=TURQ_OSC)
            c.alignment = Alignment(horizontal="center", vertical="center")
            c.border = BORDE
            for j, (_, letra) in enumerate(mapeo, start=2):
                cc = hoja.cell(row=f, column=j, value=(
                    f'=IFERROR(INDEX(Requerimientos!${letra}:${letra},'
                    f'MATCH($A{f},Requerimientos!$A:$A,0)),"")'))
                cc.font = F()
                cc.border = BORDE
                cc.alignment = Alignment(
                    horizontal=("left" if j == 2 else "center"),
                    vertical="center", wrap_text=True)
            hoja.row_dimensions[f].height = 30
            f += 1
        rangos.append((ini, f - 1))
        f += 1
    for ini, fin in rangos:
        hoja.row_dimensions.group(ini, fin, outline_level=1, hidden=False)
    return f - 2

def plural(n):
    return "requerimiento" if n == 1 else "requerimientos"

grupos_rol = [(f"{rol}    ·    {len(maestra[maestra['Rol'] == rol])} "
               f"{plural(len(maestra[maestra['Rol'] == rol]))}",
               maestra[maestra["Rol"] == rol]) for rol in ORDEN_ROL]
ULTP = vista(pr, grupos_rol, MAP_P, NCP)
reglas_estado(pr, f"F6:F{ULTP}")
reglas_mockup(pr, f"G6:G{ULTP}")
reglas_prioridad(pr, f"D6:D{ULTP}")
pr.freeze_panes = "A6"
pr.sheet_view.showGridLines = False
pr.print_title_rows = "5:5"
pr.page_setup.orientation = "landscape"
pr.sheet_properties.pageSetUpPr.fitToPage = True
pr.page_setup.fitToWidth = 1

# ================================================================ 4. Plan por laboratorio
pl = wb.create_sheet("Plan por laboratorio", 3)
NCL = 8
titulo_hoja(pl, "RoomTrip · Plan por laboratorio",
            "Qué requerimientos se trabajan en cada entrega del curso", NCL,
            "Un requerimiento aparece en varios laboratorios: los labs son capas de "
            "madurez, no un reparto del alcance. La columna «Cierre» indica dónde queda terminado.")
encabezado(pl, 5, ["ID", "Requerimiento", "Rol", "Módulo", "Prioridad",
                   "Fases", "Cierre", "Estado"],
           [11, 62, 21, 22, 11, 20, 12, 13])
MAP_L = [("Requerimiento", "E"), ("Rol", "C"), ("Módulo", "B"), ("Prioridad", "G"),
         ("Fases", "H"), ("Cierre", "I"), ("Estado", "J")]

grupos_lab = []
for lab in LABS:
    sub = maestra[maestra["ID"].map(lambda i: lab in FASES[i])]
    fecha = LAB_FECHA[lab]
    txt = fecha.strftime("%d/%m/%Y") if fecha else "fecha por definir"
    cierran = (maestra["Cierre"] == LAB_NOMBRE[lab]).sum()
    verbo = "cierra" if cierran == 1 else "cierran"
    etiqueta = (f"{LAB_NOMBRE[lab]}    ·    {txt}    ·    {LAB_DESC[lab]}    ·    "
                f"{len(sub)} {plural(len(sub))}, {cierran} {verbo} aquí")
    grupos_lab.append((etiqueta, sub))
ULTL = vista(pl, grupos_lab, MAP_L, NCL)
reglas_estado(pl, f"H6:H{ULTL}")
reglas_prioridad(pl, f"E6:E{ULTL}")
pl.freeze_panes = "A6"
pl.sheet_view.showGridLines = False
pl.print_title_rows = "5:5"
pl.page_setup.orientation = "landscape"
pl.sheet_properties.pageSetUpPr.fitToPage = True
pl.page_setup.fitToWidth = 1

# ================================================================ 5. Guía
g = wb.create_sheet("Guía")
NCG = 4
titulo_hoja(g, "RoomTrip · Cómo usar este libro",
            "Matriz de requerimientos del proyecto · actualizado el 23/08/2026", NCG)
for i, w in enumerate([30, 62, 32, 24], start=1):
    g.column_dimensions[get_column_letter(i)].width = w

f = 5
banda(g, f, "Hojas del libro", NCG); f += 1
encabezado(g, f, ["Hoja", "Para qué sirve", "¿Se edita?", ""], [30, 62, 32, 24]); f += 1
for nombre, para, edita in [
    ("Resumen", "Indicadores del proyecto: avance por rol, carga por laboratorio y por módulo. Todo calculado con fórmulas.", "No. Se actualiza solo."),
    ("Requerimientos", "Tabla maestra con los 45 requerimientos y todos sus atributos. Tiene filtros y listas desplegables.", "Sí. Es la única hoja editable."),
    ("Por rol", "Los mismos requerimientos agrupados por rol, para repartir el trabajo entre el equipo.", "No. Refleja la maestra."),
    ("Plan por laboratorio", "Qué se trabaja en cada entrega del curso. Un requerimiento aparece en varias.", "No. Refleja la maestra."),
    ("Guía", "Esta hoja: significado de cada columna y convenciones.", "No."),
]:
    g.cell(row=f, column=1, value=nombre).font = F(bold=True, color=TURQ_OSC)
    g.cell(row=f, column=2, value=para).font = F()
    g.cell(row=f, column=3, value=edita).font = F()
    for col in range(1, NCG + 1):
        c = g.cell(row=f, column=col)
        c.border = BORDE
        c.alignment = Alignment(horizontal="left", vertical="center", wrap_text=True)
    g.row_dimensions[f].height = 32
    f += 1

f += 1
banda(g, f, "Cómo se leen «Fases» y «Cierre»", NCG); f += 1
for texto in [
    "Los laboratorios del curso no reparten el alcance del sistema: son capas de madurez sobre los mismos requerimientos.",
    "Ejemplo: «reservar con tarjeta» se dibuja en el Lab 3 como pantalla navegable y recién en el Lab 6 guarda la reserva de verdad. Sus fases son L3 · L6 y su cierre es el Lab 6.",
    "Por eso la suma de la columna «Se trabaja» del Resumen es mayor que 45. No es un error.",
    "Para saber qué se entrega en una fecha concreta, mira «Cierre», no «Fases».",
]:
    g.merge_cells(start_row=f, start_column=1, end_row=f, end_column=NCG)
    c = g.cell(row=f, column=1, value="•   " + texto)
    c.font = F()
    c.alignment = Alignment(horizontal="left", vertical="center", indent=1, wrap_text=True)
    g.row_dimensions[f].height = 24
    f += 1

f += 1
banda(g, f, "Significado de las columnas", NCG); f += 1
encabezado(g, f, ["Columna", "Qué contiene", "Valores admitidos", ""], [30, 62, 32, 24]); f += 1
for nombre, que, val in [
    ("ID", "Identificador trazable con el enunciado del curso.", "RF-<ROL>-<NN> · RNF-<NN>"),
    ("Módulo", "Agrupación funcional del sistema.", "M1 a M10"),
    ("Rol", "Quién ejecuta el requerimiento.", "Los 4 roles + Transversal"),
    ("Tipo", "Naturaleza del requerimiento.", "Funcional · No funcional"),
    ("Requerimiento", "Enunciado en una línea.", "Texto libre"),
    ("Criterio de aceptación", "Cómo se comprueba que está cumplido. Es lo que lo hace evaluable.", "Texto libre"),
    ("Prioridad", "Urgencia relativa.", "Alta · Media · Baja"),
    ("Fases", "Laboratorios en los que se trabaja el requerimiento.", "L3 · L4 · L5 · L6 · L7 · Web"),
    ("Cierre", "Laboratorio en el que queda terminado.", "Lab 3 a Lab 7 · App web"),
    ("Estado", "Avance real.", "Pendiente · En progreso · Hecho · Bloqueado"),
    ("Mockup", "Si la pantalla ya está diseñada en Figma.", "Sí · Parcial · No · NA"),
    ("Responsable", "Integrante del equipo a cargo.", "Nombre"),
    ("Observaciones", "Notas, riesgos y detalles pendientes.", "Texto libre"),
]:
    g.cell(row=f, column=1, value=nombre).font = F(bold=True, color=TURQ_OSC)
    g.cell(row=f, column=2, value=que).font = F()
    g.cell(row=f, column=3, value=val).font = F()
    for col in range(1, NCG + 1):
        c = g.cell(row=f, column=col)
        c.border = BORDE
        c.alignment = Alignment(horizontal="left", vertical="center", wrap_text=True)
    g.row_dimensions[f].height = 28
    f += 1

f += 1
banda(g, f, "Convenciones de trabajo", NCG); f += 1
for n in [
    "Editar únicamente la hoja «Requerimientos». Las demás se actualizan solas.",
    "Al añadir un requerimiento, insertar la fila dentro del rango existente para que las fórmulas del Resumen lo tomen.",
    "Los IDs no se reutilizan: si un requerimiento se elimina, su ID queda retirado.",
    "Los cambios de este libro se versionan en el repositorio junto con docs/requerimientos.md.",
]:
    g.merge_cells(start_row=f, start_column=1, end_row=f, end_column=NCG)
    c = g.cell(row=f, column=1, value="•   " + n)
    c.font = F()
    c.alignment = Alignment(horizontal="left", vertical="center", indent=1, wrap_text=True)
    g.row_dimensions[f].height = 22
    f += 1

g.sheet_view.showGridLines = False

wb.move_sheet("Guía", offset=-4)
wb.active = 1
wb.save(OUT)

print("Generado:", OUT)
print("Hojas:", wb.sheetnames)
print()
print("Carga por laboratorio:")
for lab in LABS:
    trabaja = sum(1 for i in maestra["ID"] if lab in FASES[i])
    cierra = int((maestra["Cierre"] == LAB_NOMBRE[lab]).sum())
    print(f"  {LAB_NOMBRE[lab]:<10} se trabaja: {trabaja:<3} cierra: {cierra}")
print()
print("Total requerimientos:", len(maestra))
