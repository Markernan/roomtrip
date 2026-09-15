package pe.pucp.tel05.taxi.taxista.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import pe.pucp.tel05.taxi.taxista.domain.Administrador;

@Repository
public interface AdministradorRepository extends MongoRepository<Administrador, String> {

    Optional<Administrador> findByCorreo(String correo);
}
