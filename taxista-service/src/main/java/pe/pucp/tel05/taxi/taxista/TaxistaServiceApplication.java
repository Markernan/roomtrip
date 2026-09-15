package pe.pucp.tel05.taxi.taxista;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Microservicio duenio del ciclo de vida del taxista.
 *
 * <p>Base de datos propia (taxi_registry), independiente del Firebase que usa
 * la app movil de reservas. Cumple el RF-WTX-008 y la restriccion RES-04.
 */
@SpringBootApplication
public class TaxistaServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaxistaServiceApplication.class, args);
    }
}
