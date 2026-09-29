package com.example.roomtrip.data.model;

/**
 * Estado de una cuenta u hotel que el Superadmin puede activar o desactivar. RF-USR-007 y RN-015.
 *
 * <p>Un elemento INACTIVO conserva su información histórica pero no puede operar.
 */
public enum EstadoCuenta {
    ACTIVO("Activo"),
    INACTIVO("Inactivo");

    private final String etiqueta;

    EstadoCuenta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    /** Texto que se muestra al usuario. */
    public String getEtiqueta() {
        return etiqueta;
    }

    public boolean esActivo() {
        return this == ACTIVO;
    }

    /** Convierte el valor de un switch o checkbox ("activado") al estado correspondiente. */
    public static EstadoCuenta desde(boolean activo) {
        return activo ? ACTIVO : INACTIVO;
    }
}
