package pe.pucp.tel05.taxi.taxista.web.dto;

public record TokenResponse(
        String token,
        String tipo,
        String rol,
        String subject,
        long expiraEnSegundos) {

    public static TokenResponse bearer(String token, String rol, String subject, long expiraEnSegundos) {
        return new TokenResponse(token, "Bearer", rol, subject, expiraEnSegundos);
    }
}
