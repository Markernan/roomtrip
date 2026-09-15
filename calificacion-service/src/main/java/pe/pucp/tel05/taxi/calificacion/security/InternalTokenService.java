package pe.pucp.tel05.taxi.calificacion.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Firma tokens de corta duracion para las llamadas servicio-a-servicio hacia
 * taxista-service (RN-013: empujar valoracionPromedio despues de calificar).
 *
 * <p>Usa el mismo secreto compartido HS256 que ya validan los tres servicios
 * (ver taxista-service.JwtService): no hay un cuarto secreto que gestionar,
 * solo un rol nuevo (SERVICIO_INTERNO) que taxista-service exige para ese
 * endpoint puntual.
 */
@Service
public class InternalTokenService {

    private static final long EXPIRACION_SEGUNDOS = 60;

    private final byte[] secreto;

    public InternalTokenService(@Value("${app.jwt.secret}") String secreto) {
        this.secreto = secreto.getBytes(StandardCharsets.UTF_8);
    }

    /** Token de un solo uso: vive lo justo para la llamada que lo emite. */
    public String generar() {
        Instant ahora = Instant.now();
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject("calificacion-service")
                .issuer("calificacion-service")
                .claim("roles", List.of("SERVICIO_INTERNO"))
                .issueTime(Date.from(ahora))
                .expirationTime(Date.from(ahora.plus(EXPIRACION_SEGUNDOS, ChronoUnit.SECONDS)))
                .build();

        SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
        try {
            jwt.sign(new MACSigner(secreto));
        } catch (JOSEException e) {
            throw new IllegalStateException("No se pudo firmar el token interno", e);
        }
        return jwt.serialize();
    }
}
