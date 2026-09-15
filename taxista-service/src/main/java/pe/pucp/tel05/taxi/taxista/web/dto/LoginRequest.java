package pe.pucp.tel05.taxi.taxista.web.dto;

import jakarta.validation.constraints.NotBlank;

/** RF-WTX-009. */
public record LoginRequest(
        @NotBlank String correo,
        @NotBlank String password) {
}
