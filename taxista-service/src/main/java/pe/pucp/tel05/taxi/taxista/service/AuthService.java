package pe.pucp.tel05.taxi.taxista.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import pe.pucp.tel05.taxi.taxista.domain.Administrador;
import pe.pucp.tel05.taxi.taxista.domain.EstadoHabilitacion;
import pe.pucp.tel05.taxi.taxista.domain.Taxista;
import pe.pucp.tel05.taxi.taxista.repository.AdministradorRepository;
import pe.pucp.tel05.taxi.taxista.repository.TaxistaRepository;
import pe.pucp.tel05.taxi.taxista.security.JwtService;
import pe.pucp.tel05.taxi.taxista.web.dto.TokenResponse;

/**
 * RF-WTX-009: autenticacion de administradores y taxistas.
 *
 * <p>Se buscan primero los administradores y luego los taxistas. Un taxista
 * que no esta APROBADO puede iniciar sesion (para ver el estado de su
 * solicitud) pero su token no le sirve para operar: las rutas de operacion
 * exigen estado APROBADO.
 */
@Service
public class AuthService {

    private final AdministradorRepository administradores;
    private final TaxistaRepository taxistas;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(AdministradorRepository administradores,
                       TaxistaRepository taxistas,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.administradores = administradores;
        this.taxistas = taxistas;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public TokenResponse login(String correo, String password) {
        String normalizado = correo == null ? "" : correo.toLowerCase().trim();

        Optional<Administrador> admin = administradores.findByCorreo(normalizado);
        if (admin.isPresent() && passwordEncoder.matches(password, admin.get().getPasswordHash())) {
            Administrador a = admin.get();
            String token = jwtService.generar(a.getId(), a.getCorreo(), a.getRol());
            return TokenResponse.bearer(token, a.getRol(), a.getId(), jwtService.expiracionSegundos());
        }

        Optional<Taxista> taxista = taxistas.findByCorreo(normalizado);
        if (taxista.isPresent() && passwordEncoder.matches(password, taxista.get().getPasswordHash())) {
            Taxista t = taxista.get();
            if (t.getEstadoHabilitacion() == EstadoHabilitacion.RECHAZADO) {
                throw new CredencialesInvalidasException("Su solicitud de registro fue rechazada");
            }
            String token = jwtService.generar(t.getId(), t.getCorreo(), "TAXISTA");
            return TokenResponse.bearer(token, "TAXISTA", t.getId(), jwtService.expiracionSegundos());
        }

        // Mismo mensaje para usuario inexistente y contrasenia incorrecta:
        // no se filtra si un correo esta registrado o no.
        throw new CredencialesInvalidasException("Correo o contrasenia incorrectos");
    }

    /** Se traduce a HTTP 401. */
    public static class CredencialesInvalidasException extends RuntimeException {
        public CredencialesInvalidasException(String mensaje) {
            super(mensaje);
        }
    }
}
