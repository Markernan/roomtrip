package pe.pucp.tel05.taxi.taxista.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Emision de tokens HS256. El mismo secreto lo usan el gateway y
 * calificacion-service para validar, asi que vive en una sola variable de
 * entorno (JWT_SECRET) y nunca en el repositorio.
 *
 * <p>Se eligio HS256 (secreto compartido) sobre RS256 por simplicidad: con
 * tres servicios propios no hay terceros que necesiten validar sin conocer el
 * secreto. Si mas adelante federan los tokens de Firebase para la app movil,
 * ahi si conviene pasar a validacion por JWKS.
 */
@Service
public class JwtService {

    private final byte[] secreto;
    private final long expiracionMinutos;

    public JwtService(
            @Value("${app.jwt.secret}") String secreto,
            @Value("${app.jwt.expiracion-minutos:120}") long expiracionMinutos) {

        if (secreto == null || secreto.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException(
                    "JWT_SECRET debe tener al menos 32 bytes para firmar en HS256");
        }
        this.secreto = secreto.getBytes(StandardCharsets.UTF_8);
        this.expiracionMinutos = expiracionMinutos;
    }

    public String generar(String subject, String correo, String rol) {
        Instant ahora = Instant.now();
        Instant expira = ahora.plus(expiracionMinutos, ChronoUnit.MINUTES);

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(subject)
                .issuer("taxista-service")
                .claim("correo", correo)
                .claim("roles", List.of(rol))
                .issueTime(Date.from(ahora))
                .expirationTime(Date.from(expira))
                .build();

        SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
        try {
            jwt.sign(new MACSigner(secreto));
        } catch (JOSEException e) {
            throw new IllegalStateException("No se pudo firmar el token", e);
        }
        return jwt.serialize();
    }

    public long expiracionSegundos() {
        return expiracionMinutos * 60;
    }
}
