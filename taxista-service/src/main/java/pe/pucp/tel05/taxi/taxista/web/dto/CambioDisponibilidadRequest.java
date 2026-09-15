package pe.pucp.tel05.taxi.taxista.web.dto;

import jakarta.validation.constraints.NotNull;

import pe.pucp.tel05.taxi.taxista.domain.EstadoDisponibilidad;

/** RF-WTX-007: cambio de estado operativo del taxista. */
public record CambioDisponibilidadRequest(
        @NotNull(message = "El estado de disponibilidad es obligatorio")
        EstadoDisponibilidad estado) {
}
