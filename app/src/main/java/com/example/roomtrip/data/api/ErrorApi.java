package com.example.roomtrip.data.api;

/**
 * Cuerpo uniforme de los errores de la API ({@code docs/API.md}, sección 2):
 * {@code {"status":409,"error":"REGLA_NEGOCIO","mensaje":"..."}}.
 */
public class ErrorApi {

    private int status;
    private String error;
    private String mensaje;

    public ErrorApi() {
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMensaje() {
        return mensaje;
    }
}
