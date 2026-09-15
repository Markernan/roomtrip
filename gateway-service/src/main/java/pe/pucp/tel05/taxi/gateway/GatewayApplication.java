package pe.pucp.tel05.taxi.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada unico de la API REST del subsistema web de taxistas.
 *
 * <p>Responsabilidades (MOD-API + Unidad 5 del curso):
 * <ul>
 *   <li>Enrutar hacia taxista-service y calificacion-service.</li>
 *   <li>Validar el JWT antes de dejar pasar la peticion.</li>
 *   <li>Aplicar circuit breaker y devolver un fallback 503 legible
 *       cuando un servicio aguas abajo no responde (RF-TAX-018).</li>
 * </ul>
 */
@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
