package pe.pucp.tel05.taxi.gateway.web;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Respuesta de degradacion controlada cuando el circuit breaker se abre.
 *
 * <p>Es lo que hace verificable el RF-TAX-018 y el RNF-FIA-001: la app movil
 * recibe un 503 con un mensaje claro en lugar de un timeout, y puede seguir
 * operando el resto del sistema de reservas.
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    private static final String MENSAJE =
            "El servicio de gestion de taxistas no esta disponible en este momento. "
            + "Intentalo nuevamente en unos minutos.";

    @GetMapping("/{servicio}")
    public ResponseEntity<Map<String, Object>> fallbackGet(@PathVariable String servicio) {
        return respuesta(servicio);
    }

    @PostMapping("/{servicio}")
    public ResponseEntity<Map<String, Object>> fallbackPost(@PathVariable String servicio) {
        return respuesta(servicio);
    }

    private ResponseEntity<Map<String, Object>> respuesta(String servicio) {
        Map<String, Object> cuerpo = Map.of(
                "timestamp", Instant.now().toString(),
                "status", HttpStatus.SERVICE_UNAVAILABLE.value(),
                "error", "SERVICIO_NO_DISPONIBLE",
                "servicio", servicio == null ? "desconocido" : servicio,
                "mensaje", MENSAJE);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(cuerpo);
    }
}
