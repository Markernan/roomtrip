package pe.pucp.tel05.taxi.portal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Interfaz web del subsistema de taxistas.
 *
 * <p>No habla con Mongo: todo lo hace contra el gateway, igual que la app
 * Android. Asi la API queda ejercitada por dos clientes distintos y cualquier
 * hueco en el contrato aparece temprano.
 */
@SpringBootApplication
public class PortalWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(PortalWebApplication.class, args);
    }
}
