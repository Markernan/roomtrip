package com.example.roomtrip.data.model;

/**
 * Estados del servicio de taxi al aeropuerto. RN-010 y RF-TAX-008.
 *
 * <p>Los nombres coinciden con los de la API de taxistas y con el README, para que un valor
 * guardado en Firestore o recibido por REST ("EN_CAMINO") se lea con {@link #valueOf(String)}
 * sin traducciones. El texto para el usuario está en {@link #getEtiqueta()}.
 *
 * <p>El estado FINALIZADO solo se registra al validar el código QR del cliente (RN-011); eso
 * lo controla quien cambia el estado, no este enum.
 */
public enum EstadoServicioTaxi {
    SOLICITADO("Solicitado"),
    ASIGNADO("Asignado"),
    EN_CAMINO("En camino"),
    EN_TRASLADO("En traslado"),
    FINALIZADO("Finalizado");

    private final String etiqueta;

    EstadoServicioTaxi(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    /** Texto que se muestra al usuario. */
    public String getEtiqueta() {
        return etiqueta;
    }

    /** Siguiente estado de la secuencia, o {@code null} si el servicio ya está FINALIZADO. */
    public EstadoServicioTaxi siguiente() {
        EstadoServicioTaxi[] todos = values();
        return ordinal() + 1 < todos.length ? todos[ordinal() + 1] : null;
    }

    /** RN-010: solo se puede pasar al estado inmediato siguiente, sin saltarse ninguno. */
    public boolean puedePasarA(EstadoServicioTaxi destino) {
        return destino != null && destino == siguiente();
    }
}
