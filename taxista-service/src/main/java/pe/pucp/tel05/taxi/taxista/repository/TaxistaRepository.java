package pe.pucp.tel05.taxi.taxista.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import pe.pucp.tel05.taxi.taxista.domain.EstadoDisponibilidad;
import pe.pucp.tel05.taxi.taxista.domain.EstadoHabilitacion;
import pe.pucp.tel05.taxi.taxista.domain.Taxista;

@Repository
public interface TaxistaRepository extends MongoRepository<Taxista, String> {

    Optional<Taxista> findByCorreo(String correo);

    boolean existsByCorreo(String correo);

    boolean existsByNumeroDocumento(String numeroDocumento);

    boolean existsByPlaca(String placa);

    List<Taxista> findByEstadoHabilitacion(EstadoHabilitacion estadoHabilitacion);

    /**
     * RF-API-001: solo taxistas aprobados Y disponibles.
     * Los dos filtros van juntos a proposito: un taxista aprobado pero
     * EN_SERVICIO no debe aparecer como asignable.
     */
    List<Taxista> findByEstadoHabilitacionAndEstadoDisponibilidad(
            EstadoHabilitacion estadoHabilitacion,
            EstadoDisponibilidad estadoDisponibilidad);
}
