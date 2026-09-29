package com.example.roomtrip.data.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Ingreso total generado por un servicio adicional del hotel en el reporte del admin. */
public class IngresoServicio {

    private final String nombre;
    private final double monto;

    public IngresoServicio(String nombre, double monto) {
        this.nombre = nombre;
        this.monto = monto;
    }

    public String getNombre() { return nombre; }
    public double getMonto() { return monto; }

    /**
     * Regla del ERS (RF-REP-004): el reporte de ingresos por servicios adicionales se ordena de
     * menor a mayor monto. Devuelve una lista nueva y no modifica la recibida. Con montos iguales
     * se conserva el orden original.
     */
    public static List<IngresoServicio> ordenarDeMenorAMayor(List<IngresoServicio> ingresos) {
        List<IngresoServicio> copia = new ArrayList<>(ingresos);
        copia.sort(Comparator.comparingDouble(IngresoServicio::getMonto));
        return copia;
    }
}
