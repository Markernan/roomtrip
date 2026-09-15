package pe.pucp.tel05.taxi.taxista.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Llamada interna de calificacion-service tras registrar una calificacion.
 * Empuja la copia de solo lectura que vive en el documento del taxista.
 */
public record ActualizarValoracionRequest(
        @NotNull(message = "El promedio es obligatorio")
        @Min(value = 0, message = "El promedio no puede ser negativo") Double promedio,
        @NotNull(message = "El total de calificaciones es obligatorio")
        @Min(value = 0, message = "El total no puede ser negativo") Long total) {
}
