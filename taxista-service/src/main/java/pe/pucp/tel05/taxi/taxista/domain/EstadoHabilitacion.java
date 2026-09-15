package pe.pucp.tel05.taxi.taxista.domain;

/**
 * Estado administrativo del taxista frente al Superadmin.
 * RF-WTX-003 y RF-WTX-004.
 */
public enum EstadoHabilitacion {
    /** Se autoregistro y espera decision del Superadmin. No puede prestar servicios. */
    PENDIENTE,
    /** Aprobado por el Superadmin. Habilitado para prestar servicios. */
    APROBADO,
    /** Rechazado por el Superadmin. Permanece inhabilitado. */
    RECHAZADO
}
