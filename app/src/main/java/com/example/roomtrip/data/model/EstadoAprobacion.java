package com.example.roomtrip.data.model;

/**
 * Habilitación del taxista por parte del Superadmin (RF-WTX-004): solo un taxista
 * {@link #APROBADO} puede prestar servicios desde la app móvil.
 *
 * <p>Los nombres son los mismos que usa la API de taxistas ({@code PENDIENTE → APROBADO o
 * RECHAZADO}, ver {@code taxi-service/README.md}), para que en el Lab 7 el valor que llegue por REST
 * se lea con {@link #valueOf(String)} sin traducciones. En el Lab 6 se guarda en el campo
 * {@code estadoAprobacion} de {@code usuarios/{uid}}.
 */
public enum EstadoAprobacion {
    PENDIENTE("Pendiente de aprobación"),
    APROBADO("Aprobado"),
    RECHAZADO("Rechazado");

    private final String etiqueta;

    EstadoAprobacion(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    /** Texto que se muestra al usuario. */
    public String getEtiqueta() {
        return etiqueta;
    }
}
