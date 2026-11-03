package com.example.roomtrip.data.model;

/**
 * Estados de una reserva. RF-RES-010.
 *
 * <p>Todavía no hay un modelo {@code Reserva} en la app (MisReservasActivity usa objetos Hotel);
 * este enum queda listo para el módulo de reservas (Lab 4 y Lab 6). Se guarda por su nombre
 * ("EN_CURSO"); el texto para el usuario está en {@link #getEtiqueta()}.
 */
public enum EstadoReserva {
    PENDIENTE("Pendiente"),
    CONFIRMADA("Confirmada"),
    EN_CURSO("En curso"),
    FINALIZADA("Finalizada"),
    CANCELADA("Cancelada");

    private final String etiqueta;

    EstadoReserva(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    /** Texto que se muestra al usuario. */
    public String getEtiqueta() {
        return etiqueta;
    }

    /**
     * Glosario del ERS: "reserva activa" es CONFIRMADA o EN_CURSO. De esto dependen la validación
     * de no superposición (RN-001) y la ventana del chat (RN-006).
     */
    public boolean esActiva() {
        return this == CONFIRMADA || this == EN_CURSO;
    }

    /** RF-CHK-001: el checkout solo está disponible para reservas EN_CURSO. */
    public boolean permiteCheckout() {
        return this == EN_CURSO;
    }
}
