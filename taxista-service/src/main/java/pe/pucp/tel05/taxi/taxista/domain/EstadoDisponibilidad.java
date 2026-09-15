package pe.pucp.tel05.taxi.taxista.domain;

/**
 * Estado operativo del taxista. RF-WTX-007.
 *
 * <p>Solo un taxista APROBADO y DISPONIBLE puede ser devuelto por
 * GET /api/taxistas/disponibles (RF-API-001).
 */
public enum EstadoDisponibilidad {
    DISPONIBLE,
    EN_SERVICIO,
    NO_DISPONIBLE
}
