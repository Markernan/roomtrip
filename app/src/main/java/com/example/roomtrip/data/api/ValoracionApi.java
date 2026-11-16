package com.example.roomtrip.data.api;

/** Valoración promedio de un taxista. Sin calificaciones devuelve 0.0 y 0, no un error. */
public class ValoracionApi {

    private String taxistaId;
    private double promedio;
    private long totalCalificaciones;

    public ValoracionApi() {
    }

    public String getTaxistaId() {
        return taxistaId;
    }

    public double getPromedio() {
        return promedio;
    }

    public long getTotalCalificaciones() {
        return totalCalificaciones;
    }
}
