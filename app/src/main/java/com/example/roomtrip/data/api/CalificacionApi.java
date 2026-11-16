package com.example.roomtrip.data.api;

/**
 * Cuerpo y respuesta de {@code POST /api/calificaciones}. Al enviarla solo se llenan los cuatro primeros
 * campos; la respuesta trae además {@code id} y {@code fecha}.
 */
public class CalificacionApi {

    private String id;
    private String taxistaId;
    private String servicioTaxiId;
    private int puntuacion;
    private String observacion;
    private String fecha;

    public CalificacionApi() {
    }

    public CalificacionApi(String taxistaId, String servicioTaxiId, int puntuacion, String observacion) {
        this.taxistaId = taxistaId;
        this.servicioTaxiId = servicioTaxiId;
        this.puntuacion = puntuacion;
        this.observacion = observacion;
    }

    public String getId() {
        return id;
    }

    public String getTaxistaId() {
        return taxistaId;
    }

    public String getServicioTaxiId() {
        return servicioTaxiId;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public String getObservacion() {
        return observacion;
    }

    public String getFecha() {
        return fecha;
    }
}
