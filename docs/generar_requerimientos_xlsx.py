# -*- coding: utf-8 -*-
"""Genera docs/requerimientos.xlsx de RoomTrip a partir de docs/requerimientos.csv."""
import datetime as dt
import pandas as pd
from openpyxl import Workbook
from openpyxl.styles import Font, PatternFill, Alignment, Border, Side
from openpyxl.utils import get_column_letter
from openpyxl.worksheet.datavalidation import DataValidation
from openpyxl.formatting.rule import CellIsRule, DataBarRule
from openpyxl.chart import BarChart, Reference

CSV = r"C:\Users\USUARIO\Desktop\roomtrip\docs\requerimientos.csv"
OUT = r"C:\Users\USUARIO\Desktop\roomtrip\docs\requerimientos.xlsx"

# ---------------------------------------------------------------- paleta
TURQ_OSC = "00696E"   # cabeceras
TURQ = "00838F"       # titulos
TURQ_MED = "4DB6AC"
TURQ_CLA = "B2DFDB"   # bandas de seccion
TURQ_PALE = "E0F2F1"  # fondo suave
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

ENTREGABLE = {
    "3 - UI":             ("Lab 3", "Navegación y UI"),
    "4 - RecyclerView":   ("Lab 4", "RecyclerView"),
    "5 - Storage":        ("Lab 5", "Storage local"),
    "5 - Notificaciones": ("Lab 5", "Notificaciones"),
    "6 - Firebase Auth":  ("Lab 6", "Firebase Authentication"),
    "6 - Firebase DB":    ("Lab 6", "Firebase Database"),
    "7 - Prefinal":       ("Lab 7", "Presentación prefinal"),
    "Final":              ("Final", "App web de taxistas"),
}
ORDEN_ENT = ["Lab 3", "Lab 4", "Lab 5", "Lab 6", "Lab 7", "Final"]
FECHA_ENT = {
    "Lab 3": dt.date(2026, 9, 15),
    "Lab 4": dt.date(2026, 9, 29),
    "Lab 5": dt.date(2026, 10, 20),
    "Lab 6": dt.date(2026, 11, 3),
    "Lab 7": dt.date(2026, 12, 14),
    "Final": None,
}

OBSERVACIONES = {
    "RF-AH-08": "El mockup existe pero el orden del reporte debe invertirse a menor → mayor.",
    "RF-CL-01": "Faltan en el mockup el tipo de documento y la fecha de nacimiento.",
    "RF-CL-07": "Falta enganchar el chat al bottom navigation.",
    "RF-AH-01": "Los lugares históricos cercanos aún no están en el mockup.",
    "RF-CL-05": "Requiere Firebase Cloud Messaging.",
    "RF-TX-07": "Necesita librería de escaneo de QR.",
    "RF-CL-09": "Necesita Google Maps SDK y la ubicación desde Realtime Database.",
    "RF-GN-01": "Validar con una transacción; no basta con comprobarlo en el cliente.",
    "RF-GN-02": "Validar con una transacción; no basta con comprobarlo en el cliente.",
    "RF-TX-04": "Requiere transacción para garantizar la exclusividad del pedido.",
    "RNF-06": "Todo el manejo del fallo vive en TaxiRepository.",
}

df["Rol"] = df["Actor"].map(ROL_DE_ACTOR)
df["Entregable"] = df["Entregable objetivo"].map(lambda x: ENTREGABLE[x][0])
df["Foco"] = df["Entregable objetivo"].map(lambda x: ENTREGABLE[x][1])
df["Mockup"] = df["Mockup"].replace({"Corregir orden": "Parcial", "": "NA"})
df["Observaciones"] = df["ID"].map(OBSERVACIONES).fillna("")
df["Responsable"] = ""

df["_rol"] = df["Rol"].map(ORDEN_ROL.index)
df["_ent"] = df["Entregable"].map(ORDEN_ENT.index)
df["_pri"] = df["Prioridad"].map({"Alta": 0, "Media": 1, "Baja": 2})

maestra = df.sort_values(["_rol", "_ent", "_pri", "ID"]).reset_index(drop=True)

wb = Workbook()

# ================================================================ helpers
def titulo_hoja(ws, texto, subtitulo, ncols, nota=None):
    ws.merge_cells(start_row=1, start_column=1, end_row=1, end_column=ncols)
    c = ws.cell(row=1, column=1, value=texto)
    c.font = Font(name=FUENTE, size=16, bold=True, color=BLANCO)
    c.fill = PatternFill("solid", fgColor=TURQ)
    c.alignment = Alignment(horizontal="left", vertical="center", indent=1)
    ws.row_dimensions[1].height = 34
    ws.merge_cells(start_row=2, start_column=1, end_row=2, end_column=ncols)
    c = ws.cell(row=2, column=2 - 1, value=subtitulo)
    c.font = Font(name=FUENTE, size=9, color=BLANCO)
    c.fill = PatternFill("solid", fgColor=TURQ_MED)
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
    ws.conditional_formatting.add(rango, CellIsRule(operator="equal", formula=['"Hecho"'],
        fill=PatternFill("solid", fgColor=VERDE_F), font=Font(name=FUENTE, size=10, bold=True, color=VERDE_T)))
    ws.conditional_formatting.add(rango, CellIsRule(operator="equal", formula=['"En progreso"'],
        fill=PatternFill("solid", fgColor=AMBAR_F), font=Font(name=FUENTE, size=10, bold=True, color=AMBAR_T)))
    ws.conditional_formatting.add(rango, CellIsRule(operator="equal", formula=['"Bloqueado"'],
        fill=PatternFill("solid", fgColor=ROJO_F), font=Font(name=FUENTE, size=10, bold=True, color=ROJO_T)))
    ws.conditional_formatting.add(rango, CellIsRule(operator="equal", formula=['"Pendiente"'],
        fill=PatternFill("solid", fgColor=GRIS_F), font=Font(name=FUENTE, size=10, color=GRIS_T)))

def reglas_mockup(ws, rango):
    ws.conditional_formatting.add(rango, CellIsRule(operator="equal", formula=['"Sí"'],
        fill=PatternFill("solid", fgColor=VERDE_F), font=Font(name=FUENTE, size=10, color=VERDE_T)))
    ws.conditional_formatting.add(rango, CellIsRule(operator="equal", formula=['"Parcial"'],
        fill=PatternFill("solid", fgColor=AMBAR_F), font=Font(name=FUENTE, size=10, color=AMBAR_T)))
    ws.conditional_formatting.add(rango, CellIsRule(operator="equal", formula=['"No"'],
        fill=PatternFill("solid", fgColor=ROJO_F), font=Font(name=FUENTE, size=10, bold=True, color=ROJO_T)))

def reglas_prioridad(ws, rango):
    ws.conditional_formatting.add(rango, CellIsRule(operator="equal", formula=['"Alta"'],
        font=Font(name=FUENTE, size=10, bold=True, color=ROJO_T)))
    ws.conditional_formatting.add(rango, CellIsRule(operator="equal", formula=['"Media"'],
        font=Font(name=FUENTE, size=10, color=AMBAR_T)))

# ================================================================ 1. Requerimientos (maestra)
ws = wb.active
ws.title = "Requerimientos"
NC = 14
HDR = ["ID", "Módulo", "Rol", "Tipo", "Requerimiento", "Criterio de aceptación",
       "Prioridad", "Entregable", "Foco técnico", "Depende de", "Estado", "Mockup",
       "Responsable", "Observaciones"]
ANCHOS = [11, 22, 21, 12, 52, 58, 10, 11, 21, 11, 13, 10, 16, 46]
titulo_hoja(ws, "RoomTrip · Matriz de requerimientos",
            "1TEL05 Servicios y Aplicaciones para IoT · PUCP · Semestre 2026-2",
            NC, "Esta hoja es la única fuente de datos. Las hojas «Por rol» y «Por entregable» "
                "la reflejan mediante fórmulas: editar aquí y allá se actualiza solo.")
FILA_HDR = 5
encabezado(ws, FILA_HDR, HDR, ANCHOS)

COLS = ["ID", "Módulo", "Rol", "Tipo", "Requerimiento", "Criterio de aceptación",
        "Prioridad", "Entregable", "Foco", "Depende de", "Estado", "Mockup",
        "Responsable", "Observaciones"]
r = FILA_HDR + 1
fila_de_id = {}
for _, row in maestra.iterrows():
    fila_de_id[row["ID"]] = r
    for i, col in enumerate(COLS, start=1):
        c = ws.cell(row=r, column=i, value=row[col])
        c.font = F()
        c.border = BORDE
        if i in (1, 3, 4, 7, 8, 10, 11, 12):
            c.alignment = Alignment(horizontal="center", vertical="center", wrap_text=True)
        else:
            c.alignment = Alignment(horizontal="left", vertical="top", wrap_text=True)
    ws.cell(row=r, column=1).font = F(bold=True, color=TURQ_OSC)
    ws.row_dimensions[r].height = 42
    r += 1
ULT = r - 1

ws.auto_filter.ref = f"A{FILA_HDR}:N{ULT}"
ws.freeze_panes = "B6"
reglas_estado(ws, f"K6:K{ULT}")
reglas_mockup(ws, f"L6:L{ULT}")
reglas_prioridad(ws, f"G6:G{ULT}")

dv_estado = DataValidation(type="list", formula1='"Pendiente,En progreso,Hecho,Bloqueado"', allow_blank=False)
dv_pri = DataValidation(type="list", formula1='"Alta,Media,Baja"', allow_blank=False)
dv_mock = DataValidation(type="list", formula1='"Sí,Parcial,No,NA"', allow_blank=False)
for dv, rng in ((dv_estado, f"K6:K{ULT}"), (dv_pri, f"G6:G{ULT}"), (dv_mock, f"L6:L{ULT}")):
    ws.add_data_validation(dv)
    dv.add(rng)

ws.sheet_view.showGridLines = False
ws.print_title_rows = f"{FILA_HDR}:{FILA_HDR}"
ws.page_setup.orientation = "landscape"
ws.page_setup.fitToWidth = 1
ws.sheet_properties.pageSetUpPr.fitToPage = True

# ================================================================ 2. Resumen
rs = wb.create_sheet("Resumen", 0)
NCR = 9
titulo_hoja(rs, "RoomTrip · Resumen de requerimientos",
            "Todos los valores se calculan con fórmulas sobre la hoja Requerimientos", NCR,
            "Actualiza automáticamente al cambiar el estado de un requerimiento.")
for i, w in enumerate([26, 12, 12, 12, 14, 12, 12, 14, 14], start=1):
    rs.column_dimensions[get_column_letter(i)].width = w

R = f"Requerimientos!$A$6:$A${ULT}"
ROL_R = f"Requerimientos!$C$6:$C${ULT}"
TIPO_R = f"Requerimientos!$D$6:$D${ULT}"
PRI_R = f"Requerimientos!$G$6:$G${ULT}"
ENT_R = f"Requerimientos!$H$6:$H${ULT}"
EST_R = f"Requerimientos!$K$6:$K${ULT}"
MOC_R = f"Requerimientos!$L$6:$L${ULT}"
MOD_R = f"Requerimientos!$B$6:$B${ULT}"

def kpi(fila, col, etiqueta, formula, fmt="0", destaque=False):
    c = rs.cell(row=fila, column=col, value=etiqueta)
    c.font = F(9, color=TURQ_OSC)
    c.alignment = Alignment(horizontal="center", vertical="center", wrap_text=True)
    c.fill = PatternFill("solid", fgColor=TURQ_PALE)
    c.border = BORDE
    v = rs.cell(row=fila + 1, column=col, value=formula)
    v.font = Font(name=FUENTE, size=18, bold=True, color=TURQ if not destaque else ROJO_T)
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
kpi(6, 8, "% de avance", f"=IFERROR(COUNTIF({EST_R},\"Hecho\")/COUNTA({R}),0)", "0.0%")
kpi(6, 9, "Mockups faltantes", f'=COUNTIF({MOC_R},"No")', "0", destaque=True)

# --- por rol
banda(rs, 9, "Requerimientos por rol", NCR)
hdr_rol = ["Rol", "Total", "Alta", "Media", "Pendiente", "En progreso", "Hecho", "% avance", "Sin mockup"]
encabezado(rs, 10, hdr_rol, [26, 12, 12, 12, 14, 12, 12, 14, 14])
f0 = 11
for i, rol in enumerate(ORDEN_ROL):
    f = f0 + i
    rs.cell(row=f, column=1, value=rol).font = F(bold=True)
    rs.cell(row=f, column=2, value=f'=COUNTIF({ROL_R},$A{f})')
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

# --- por entregable
f1 = f_tot + 2
banda(rs, f1, "Requerimientos por entregable", NCR)
hdr_ent = ["Entregable", "Fecha", "Días restantes", "Total", "Alta", "Pendiente", "Hecho", "% avance", "Sin mockup"]
encabezado(rs, f1 + 1, hdr_ent, [26, 12, 14, 12, 12, 14, 12, 14, 14])
f2 = f1 + 2
for i, ent in enumerate(ORDEN_ENT):
    f = f2 + i
    rs.cell(row=f, column=1, value=ent).font = F(bold=True)
    fecha = FECHA_ENT[ent]
    if fecha:
        cf = rs.cell(row=f, column=2, value=fecha)
        cf.number_format = "DD/MM/YYYY"
        rs.cell(row=f, column=3, value=f"=$B{f}-TODAY()")
    else:
        rs.cell(row=f, column=2, value="Por definir")
        rs.cell(row=f, column=3, value="—")
    rs.cell(row=f, column=4, value=f'=COUNTIF({ENT_R},$A{f})')
    rs.cell(row=f, column=5, value=f'=COUNTIFS({ENT_R},$A{f},{PRI_R},"Alta")')
    rs.cell(row=f, column=6, value=f'=COUNTIFS({ENT_R},$A{f},{EST_R},"Pendiente")')
    rs.cell(row=f, column=7, value=f'=COUNTIFS({ENT_R},$A{f},{EST_R},"Hecho")')
    rs.cell(row=f, column=8, value=f"=IFERROR($G{f}/$D{f},0)")
    rs.cell(row=f, column=9, value=f'=COUNTIFS({ENT_R},$A{f},{MOC_R},"No")')
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
rs.conditional_formatting.add(f"H{f2}:H{f2+len(ORDEN_ENT)-1}",
    DataBarRule(start_type="num", start_value=0, end_type="num", end_value=1, color=TURQ_MED))

# --- por modulo
f3 = f2 + len(ORDEN_ENT) + 1
banda(rs, f3, "Requerimientos por módulo funcional", NCR)
encabezado(rs, f3 + 1, ["Módulo", "Total", "Alta", "Pendiente", "Hecho", "% avance", "", "", ""],
           [26, 12, 12, 14, 12, 14, 12, 14, 14])
modulos = sorted(maestra["Módulo"].unique(), key=lambda m: int(m.split()[0][1:]))
f4 = f3 + 2
for i, mod in enumerate(modulos):
    f = f4 + i
    rs.cell(row=f, column=1, value=mod).font = F()
    rs.cell(row=f, column=2, value=f'=COUNTIF({MOD_R},$A{f})')
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
datos = Reference(rs, min_col=2, min_row=10, max_row=f_tot - 1)
cats = Reference(rs, min_col=1, min_row=f0, max_row=f_tot - 1)
ch.add_data(datos, titles_from_data=True)
ch.set_categories(cats)
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
HDRP = ["ID", "Requerimiento", "Módulo", "Prioridad", "Entregable", "Estado", "Mockup", "Responsable"]
ANCHP = [11, 68, 22, 11, 12, 13, 10, 18]
encabezado(pr, 5, HDRP, ANCHP)
MAP = {"Requerimiento": "E", "Módulo": "B", "Prioridad": "G",
       "Entregable": "H", "Estado": "K", "Mockup": "L", "Responsable": "M"}
COLS_P = ["Requerimiento", "Módulo", "Prioridad", "Entregable", "Estado", "Mockup", "Responsable"]

f = 6
grupos_rol = []
for rol in ORDEN_ROL:
    sub = maestra[maestra["Rol"] == rol]
    plural = "requerimiento" if len(sub) == 1 else "requerimientos"
    banda(pr, f, f"{rol}    ·    {len(sub)} {plural}", NCP)
    ini = f + 1
    f += 1
    for _, row in sub.iterrows():
        c = pr.cell(row=f, column=1, value=row["ID"])
        c.font = F(bold=True, color=TURQ_OSC)
        c.alignment = Alignment(horizontal="center", vertical="center")
        c.border = BORDE
        for j, col in enumerate(COLS_P, start=2):
            letra = MAP[col]
            formula = (f'=IFERROR(INDEX(Requerimientos!${letra}:${letra},'
                       f'MATCH($A{f},Requerimientos!$A:$A,0)),"")')
            cc = pr.cell(row=f, column=j, value=formula)
            cc.font = F()
            cc.border = BORDE
            if j == 2:
                cc.alignment = Alignment(horizontal="left", vertical="center", wrap_text=True)
            else:
                cc.alignment = Alignment(horizontal="center", vertical="center", wrap_text=True)
        pr.row_dimensions[f].height = 30
        f += 1
    grupos_rol.append((ini, f - 1))
    f += 1
ULTP = f - 2
for ini, fin in grupos_rol:
    pr.row_dimensions.group(ini, fin, outline_level=1, hidden=False)
reglas_estado(pr, f"F6:F{ULTP}")
reglas_mockup(pr, f"G6:G{ULTP}")
reglas_prioridad(pr, f"D6:D{ULTP}")
pr.freeze_panes = "A6"
pr.sheet_view.showGridLines = False
pr.print_title_rows = "5:5"
pr.page_setup.orientation = "landscape"
pr.sheet_properties.pageSetUpPr.fitToPage = True
pr.page_setup.fitToWidth = 1

# ================================================================ 4. Por entregable
pe = wb.create_sheet("Por entregable", 3)
NCE = 8
titulo_hoja(pe, "RoomTrip · Plan por entregable",
            "Qué requerimiento se implementa en cada laboratorio del curso", NCE,
            "Vista de lectura. El contenido se trae por fórmula desde la hoja «Requerimientos».")
HDRE = ["ID", "Requerimiento", "Rol", "Foco técnico", "Prioridad", "Estado", "Mockup", "Depende de"]
ANCHE = [11, 66, 21, 21, 11, 13, 10, 12]
encabezado(pe, 5, HDRE, ANCHE)
MAPE = {"Requerimiento": "E", "Rol": "C", "Foco técnico": "I", "Prioridad": "G",
        "Estado": "K", "Mockup": "L", "Depende de": "J"}
COLS_E = ["Requerimiento", "Rol", "Foco técnico", "Prioridad", "Estado", "Mockup", "Depende de"]

f = 6
grupos_ent = []
for ent in ORDEN_ENT:
    sub = maestra[maestra["Entregable"] == ent]
    fecha = FECHA_ENT[ent]
    txt_fecha = fecha.strftime("%d/%m/%Y") if fecha else "fecha por definir"
    plural = "requerimiento" if len(sub) == 1 else "requerimientos"
    banda(pe, f, f"{ent}    ·    {txt_fecha}    ·    {len(sub)} {plural}", NCE)
    ini = f + 1
    f += 1
    for _, row in sub.iterrows():
        c = pe.cell(row=f, column=1, value=row["ID"])
        c.font = F(bold=True, color=TURQ_OSC)
        c.alignment = Alignment(horizontal="center", vertical="center")
        c.border = BORDE
        for j, col in enumerate(COLS_E, start=2):
            letra = MAPE[col]
            formula = (f'=IFERROR(INDEX(Requerimientos!${letra}:${letra},'
                       f'MATCH($A{f},Requerimientos!$A:$A,0)),"")')
            cc = pe.cell(row=f, column=j, value=formula)
            cc.font = F()
            cc.border = BORDE
            if j == 2:
                cc.alignment = Alignment(horizontal="left", vertical="center", wrap_text=True)
            else:
                cc.alignment = Alignment(horizontal="center", vertical="center", wrap_text=True)
        pe.row_dimensions[f].height = 30
        f += 1
    grupos_ent.append((ini, f - 1))
    f += 1
ULTE = f - 2
for ini, fin in grupos_ent:
    pe.row_dimensions.group(ini, fin, outline_level=1, hidden=False)
reglas_estado(pe, f"F6:F{ULTE}")
reglas_mockup(pe, f"G6:G{ULTE}")
reglas_prioridad(pe, f"E6:E{ULTE}")
pe.freeze_panes = "A6"
pe.sheet_view.showGridLines = False
pe.print_title_rows = "5:5"
pe.page_setup.orientation = "landscape"
pe.sheet_properties.pageSetUpPr.fitToPage = True
pe.page_setup.fitToWidth = 1

# ================================================================ 5. Guía
g = wb.create_sheet("Guía")
NCG = 4
titulo_hoja(g, "RoomTrip · Cómo usar este libro",
            "Matriz de requerimientos del proyecto · versión inicial 23/08/2026", NCG)
for i, w in enumerate([28, 60, 30, 30], start=1):
    g.column_dimensions[get_column_letter(i)].width = w

f = 5
banda(g, f, "Hojas del libro", NCG); f += 1
encabezado(g, f, ["Hoja", "Para qué sirve", "¿Se edita?", ""], [28, 60, 30, 30]); f += 1
hojas = [
    ("Resumen", "Indicadores del proyecto: avance por rol, por entregable y por módulo. Todo calculado con fórmulas.", "No. Se actualiza solo."),
    ("Requerimientos", "Tabla maestra con los 45 requerimientos y todos sus atributos. Tiene filtros y listas desplegables.", "Sí. Es la única hoja editable."),
    ("Por rol", "Los mismos requerimientos agrupados por rol, para repartir el trabajo entre el equipo.", "No. Refleja la maestra."),
    ("Por entregable", "Los mismos requerimientos agrupados por laboratorio, con la fecha de cada entrega.", "No. Refleja la maestra."),
    ("Guía", "Esta hoja: significado de cada columna y convenciones.", "No."),
]
for nombre, para, edita in hojas:
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
banda(g, f, "Significado de las columnas", NCG); f += 1
encabezado(g, f, ["Columna", "Qué contiene", "Valores admitidos", ""], [28, 60, 30, 30]); f += 1
cols_doc = [
    ("ID", "Identificador trazable con el enunciado del curso.", "RF-<ROL>-<NN> · RNF-<NN>"),
    ("Módulo", "Agrupación funcional del sistema.", "M1 a M10"),
    ("Rol", "Quién ejecuta el requerimiento.", "Los 4 roles + Transversal"),
    ("Tipo", "Naturaleza del requerimiento.", "Funcional · No funcional"),
    ("Requerimiento", "Enunciado en una línea.", "Texto libre"),
    ("Criterio de aceptación", "Cómo se comprueba que está cumplido. Es lo que lo hace evaluable.", "Texto libre"),
    ("Prioridad", "Urgencia relativa.", "Alta · Media · Baja"),
    ("Entregable", "Laboratorio en el que se implementa.", "Lab 3 a Lab 7 · Final"),
    ("Foco técnico", "Tecnología principal que exige ese entregable.", "Texto"),
    ("Depende de", "ID del requerimiento que debe existir antes. Define el orden de trabajo.", "Un ID o vacío"),
    ("Estado", "Avance real.", "Pendiente · En progreso · Hecho · Bloqueado"),
    ("Mockup", "Si la pantalla ya está diseñada en Figma.", "Sí · Parcial · No · NA"),
    ("Responsable", "Integrante del equipo a cargo.", "Nombre"),
    ("Observaciones", "Notas, riesgos y detalles pendientes.", "Texto libre"),
]
for nombre, que, val in cols_doc:
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
notas = [
    "Editar únicamente la hoja «Requerimientos». Las demás se actualizan solas.",
    "Al añadir un requerimiento nuevo, insertar la fila dentro del rango existente para que las fórmulas del Resumen lo tomen.",
    "Los IDs no se reutilizan: si un requerimiento se elimina, su ID queda retirado.",
    "El campo «Depende de» marca el orden real de implementación; revisarlo antes de repartir tareas.",
    "Los cambios de este libro se versionan en el repositorio junto con docs/requerimientos.md.",
]
for n in notas:
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
print("Filas de datos:", ULT - 5)
