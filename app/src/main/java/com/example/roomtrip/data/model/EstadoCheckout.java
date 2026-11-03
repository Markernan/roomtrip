package com.example.roomtrip.data.model;

/**
 * Estado de un checkout en la lista del admin de hotel.
 *
 * <p>El ERS no define estos estados por nombre: RF-CHK-003 y RF-CHK-004 hablan de un checkout
 * "pendiente de cobro" que el admin cobra. Este enum formaliza lo que ya muestra la interfaz
 * (PENDIENTE hasta que se procesa el cobro, PROCESADO después) y se puede renombrar cuando el
 * equipo lo decida; el compilador señala cada uso.
 */
public enum EstadoCheckout {
    PENDIENTE("Pendiente"),
    PROCESADO("Procesado");

    private final String etiqueta;

    EstadoCheckout(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    /** Texto que se muestra al usuario. */
    public String getEtiqueta() {
        return etiqueta;
    }

    public boolean estaProcesado() {
        return this == PROCESADO;
    }
}
