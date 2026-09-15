package pe.pucp.tel05.taxi.taxista.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Usuario administrativo del subsistema web (RF-WTX-009).
 *
 * <p>Dos roles viven aqui:
 * <ul>
 *   <li>SUPERADMIN: aprueba y rechaza solicitudes desde el portal.</li>
 *   <li>APP_MOVIL: credencial de servicio que usa la app Android para
 *       consumir la API REST. Asi la app no viaja sin autenticar.</li>
 * </ul>
 */
@Document(collection = "administradores")
public class Administrador {

    @Id
    private String id;

    @Indexed(unique = true)
    private String correo;

    private String passwordHash;

    private String rol;

    public Administrador() {
    }

    public Administrador(String correo, String passwordHash, String rol) {
        this.correo = correo;
        this.passwordHash = passwordHash;
        this.rol = rol;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
