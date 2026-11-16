package com.example.roomtrip.data.api;

/** Cuerpo de {@code PATCH /api/taxistas/{id}/disponibilidad}. */
public class DisponibilidadApi {

    private String estado;

    public DisponibilidadApi() {
    }

    public DisponibilidadApi(String estado) {
        this.estado = estado;
    }

    public String getEstado() {
        return estado;
    }
}
