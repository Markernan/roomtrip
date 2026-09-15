package pe.pucp.tel05.taxi.calificacion.web.dto;

/** RN-013: valoracion promedio del taxista. */
public record ValoracionResponse(
        String taxistaId,
        double promedio,
        long totalCalificaciones) {
}
