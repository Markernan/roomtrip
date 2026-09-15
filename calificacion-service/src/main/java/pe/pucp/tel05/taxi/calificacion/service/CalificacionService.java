package pe.pucp.tel05.taxi.calificacion.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import pe.pucp.tel05.taxi.calificacion.client.TaxistaClient;
import pe.pucp.tel05.taxi.calificacion.domain.Calificacion;
import pe.pucp.tel05.taxi.calificacion.repository.CalificacionRepository;
import pe.pucp.tel05.taxi.calificacion.web.dto.ValoracionResponse;

@Service
public class CalificacionService {

    private static final Logger log = LoggerFactory.getLogger(CalificacionService.class);

    private final CalificacionRepository repositorio;
    private final TaxistaClient taxistaClient;

    public CalificacionService(CalificacionRepository repositorio, TaxistaClient taxistaClient) {
        this.repositorio = repositorio;
        this.taxistaClient = taxistaClient;
    }

    /**
     * RF-WTX-006 y RF-TAX-017. Idempotente por servicioTaxiId: si la app movil
     * reintenta, no se duplica la calificacion ni se distorsiona el promedio.
     */
    public Calificacion registrar(String taxistaId, String servicioTaxiId,
                                  int puntuacion, String observacion) {
        if (puntuacion < 1 || puntuacion > 5) {
            throw new ReglaNegocioException("La puntuacion debe estar entre 1 y 5");
        }
        if (repositorio.existsByServicioTaxiId(servicioTaxiId)) {
            throw new ReglaNegocioException("Ese servicio de taxi ya fue calificado");
        }

        Calificacion guardada = repositorio.save(
                new Calificacion(taxistaId, servicioTaxiId, puntuacion, observacion));
        log.info("Calificacion registrada taxista={} servicio={} puntuacion={}",
                taxistaId, servicioTaxiId, puntuacion);

        ValoracionResponse actualizada = valoracion(taxistaId);
        taxistaClient.actualizarValoracion(
                taxistaId, actualizada.promedio(), actualizada.totalCalificaciones());

        return guardada;
    }

    /**
     * RN-013: media aritmetica de las calificaciones registradas.
     * Un taxista sin calificaciones devuelve 0.0 y total 0, no un error:
     * es el estado normal de un taxista recien aprobado.
     */
    public ValoracionResponse valoracion(String taxistaId) {
        List<Calificacion> calificaciones = repositorio.findByTaxistaId(taxistaId);

        if (calificaciones.isEmpty()) {
            return new ValoracionResponse(taxistaId, 0.0, 0);
        }

        double suma = calificaciones.stream().mapToInt(Calificacion::getPuntuacion).sum();
        double promedio = suma / calificaciones.size();
        // Se redondea a dos decimales para que la app movil muestre "4.33" y no
        // "4.333333333333333".
        double redondeado = Math.round(promedio * 100.0) / 100.0;

        return new ValoracionResponse(taxistaId, redondeado, calificaciones.size());
    }

    public List<Calificacion> listarPorTaxista(String taxistaId) {
        return repositorio.findByTaxistaId(taxistaId);
    }

    public static class ReglaNegocioException extends RuntimeException {
        public ReglaNegocioException(String mensaje) {
            super(mensaje);
        }
    }
}
