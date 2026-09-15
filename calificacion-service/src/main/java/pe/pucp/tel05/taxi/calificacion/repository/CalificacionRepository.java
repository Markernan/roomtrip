package pe.pucp.tel05.taxi.calificacion.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import pe.pucp.tel05.taxi.calificacion.domain.Calificacion;

@Repository
public interface CalificacionRepository extends MongoRepository<Calificacion, String> {

    List<Calificacion> findByTaxistaId(String taxistaId);

    boolean existsByServicioTaxiId(String servicioTaxiId);
}
