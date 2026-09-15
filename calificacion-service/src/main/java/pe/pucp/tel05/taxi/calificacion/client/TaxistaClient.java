package pe.pucp.tel05.taxi.calificacion.client;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import pe.pucp.tel05.taxi.calificacion.security.InternalTokenService;

/**
 * Empuja la valoracion recalculada hacia taxista-service (RN-013).
 *
 * <p>Se llama directo al servicio, no a traves del gateway: es trafico
 * interno entre dos microservicios de dominio, no una peticion de un cliente
 * publico. Es best-effort: si taxista-service no responde, se registra el
 * fallo y se sigue, porque calificacion-service ya es la fuente de verdad de
 * la calificacion y el dato en taxista-service es una copia eventualmente
 * consistente (ver Taxista.valoracionPromedio).
 */
@Component
public class TaxistaClient {

    private static final Logger log = LoggerFactory.getLogger(TaxistaClient.class);

    private final RestClient restClient;
    private final InternalTokenService tokenService;

    public TaxistaClient(@Value("${app.servicios.taxista-url}") String taxistaServiceUrl,
                         InternalTokenService tokenService) {
        this.restClient = RestClient.builder().baseUrl(taxistaServiceUrl).build();
        this.tokenService = tokenService;
    }

    public void actualizarValoracion(String taxistaId, double promedio, long total) {
        try {
            restClient.patch()
                    .uri("/api/taxistas/{id}/valoracion", taxistaId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenService.generar())
                    .body(Map.of("promedio", promedio, "total", total))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.warn("No se pudo sincronizar la valoracion del taxista {} hacia taxista-service: {}",
                    taxistaId, e.getMessage());
        }
    }
}
