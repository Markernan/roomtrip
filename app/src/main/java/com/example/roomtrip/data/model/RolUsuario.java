package com.example.roomtrip.data.model;

/**
 * Rol del usuario autenticado. Se guarda por su nombre en el campo {@code rol} de
 * {@code usuarios/{uid}} en Firestore: tras el login se lee y se navega a la pantalla del rol.
 *
 * <p>Las reglas de seguridad de Firestore ({@code firestore.rules}) comparan contra estos mismos
 * nombres, así que no se renombran. El texto para el usuario sale de {@link #getEtiqueta()}.
 */
public enum RolUsuario {
    CLIENTE("Cliente"),
    ADMIN_HOTEL("Administrador de hotel"),
    TAXISTA("Taxista"),
    SUPERADMIN("Superadmin");

    private final String etiqueta;

    RolUsuario(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    /** Texto que se muestra al usuario. */
    public String getEtiqueta() {
        return etiqueta;
    }
}
