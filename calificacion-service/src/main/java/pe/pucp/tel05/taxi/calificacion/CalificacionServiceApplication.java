package pe.pucp.tel05.taxi.calificacion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Microservicio duenio de las calificaciones del taxista.
 *
 * <p>Se separo de taxista-service a proposito: las calificaciones crecen sin
 * limite (una por servicio prestado) mientras que el padron de taxistas es
 * chico y estable. Separarlos permite escalarlos distinto y es el corte que
 * mejor justifica la arquitectura de microservicios ante el jurado.
 */
@SpringBootApplication
public class CalificacionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CalificacionServiceApplication.class, args);
    }
}
