package pe.pucp.tel05.taxi.taxista.web.dto;

import jakarta.validation.constraints.NotNull;

/**
 * RF-WTX-004: el Superadmin aprueba o rechaza una solicitud pendiente.
 * El motivo solo tiene sentido cuando aprobado = false.
 */
public record DecisionAprobacionRequest(
        @NotNull(message = "Debe indicar si aprueba o rechaza") Boolean aprobado,
        String motivo) {
}
