package pe.pucp.tel05.taxi.calificacion.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/** RF-TAX-016 y RF-TAX-017: la app movil registra la calificacion del cliente. */
public record RegistrarCalificacionRequest(
        @NotBlank(message = "El taxistaId es obligatorio") String taxistaId,
        @NotBlank(message = "El servicioTaxiId es obligatorio") String servicioTaxiId,
        @Min(value = 1, message = "La puntuacion minima es 1")
        @Max(value = 5, message = "La puntuacion maxima es 5") int puntuacion,
        String observacion) {
}
