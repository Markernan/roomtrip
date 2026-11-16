package com.example.roomtrip.data.api;

/** Respuesta de {@code POST /api/auth/login}. */
public class TokenApi {

    private String token;
    private String tipo;
    private String rol;
    private String subject;
    private long expiraEnSegundos;

    public TokenApi() {
    }

    public String getToken() {
        return token;
    }

    public String getRol() {
        return rol;
    }

    public String getSubject() {
        return subject;
    }

    public long getExpiraEnSegundos() {
        return expiraEnSegundos;
    }
}
