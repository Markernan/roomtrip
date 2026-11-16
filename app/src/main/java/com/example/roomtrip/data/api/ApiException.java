package com.example.roomtrip.data.api;

import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Response;

/**
 * Error al hablar con la API de taxistas, ya traducido a algo que se le puede mostrar al usuario.
 *
 * <p>El caso que importa es {@link Tipo#NO_DISPONIBLE} (RF-TAX-018, RNF-FIA-001): si el servicio de
 * taxistas no responde, la app avisa al usuario y <b>el resto del sistema de reservas sigue
 * funcionando</b>. Cuenta como "no disponible" todo lo que no es culpa de la petición: sin red, tiempo
 * agotado y cualquier 5xx (el gateway responde 503 con el circuito abierto; tras un rato sin uso, el
 * servicio tarda unos segundos en despertar y puede dar 500 o 503).
 */
public class ApiException extends Exception {

    /** Mismo texto que devuelve el gateway, para que el usuario vea siempre lo mismo. */
    public static final String MENSAJE_NO_DISPONIBLE =
            "El servicio de gestión de taxistas no está disponible en este momento. "
                    + "Inténtalo nuevamente en unos minutos.";

    public enum Tipo {
        /** Sin red, tiempo agotado o error 5xx del servidor. */
        NO_DISPONIBLE,
        /** 401: la credencial de servicio de la app no es válida. */
        NO_AUTENTICADO,
        /** 403. */
        SIN_PERMISO,
        /** 404: por ejemplo, un correo sin taxista registrado. */
        NO_ENCONTRADO,
        /** 409: la operación viola una regla (ya calificado, taxista no aprobado...). */
        REGLA_NEGOCIO,
        /** 400. */
        DATOS_INVALIDOS,
        /** 429: demasiados intentos de login. */
        DEMASIADOS_INTENTOS,
        /** Faltan la URL o la credencial en {@code local.properties}. */
        NO_CONFIGURADA,
        ERROR
    }

    private static final Gson GSON = new Gson();

    private final Tipo tipo;
    private final int codigoHttp;

    public ApiException(Tipo tipo, String mensaje, int codigoHttp) {
        super(mensaje);
        this.tipo = tipo;
        this.codigoHttp = codigoHttp;
    }

    public ApiException(Tipo tipo, String mensaje, int codigoHttp, Throwable causa) {
        super(mensaje, causa);
        this.tipo = tipo;
        this.codigoHttp = codigoHttp;
    }

    public Tipo getTipo() {
        return tipo;
    }

    /** Código HTTP, o 0 si el error no vino de una respuesta (sin red, sin configuración). */
    public int getCodigoHttp() {
        return codigoHttp;
    }

    public boolean esNoDisponible() {
        return tipo == Tipo.NO_DISPONIBLE;
    }

    /** Traduce una respuesta HTTP no exitosa. El mensaje del servidor, si lo hay, se conserva. */
    public static ApiException desdeRespuesta(Response<?> respuesta) {
        int codigo = respuesta.code();
        String mensajeServidor = leerMensaje(respuesta);

        if (codigo >= 500) {
            return new ApiException(Tipo.NO_DISPONIBLE, MENSAJE_NO_DISPONIBLE, codigo);
        }
        switch (codigo) {
            case 400:
                return new ApiException(Tipo.DATOS_INVALIDOS,
                        valor(mensajeServidor, "Los datos enviados no son válidos."), codigo);
            case 401:
                return new ApiException(Tipo.NO_AUTENTICADO,
                        "La app no pudo autenticarse con el servicio de taxistas.", codigo);
            case 403:
                return new ApiException(Tipo.SIN_PERMISO,
                        "No tienes permiso para realizar esta operación.", codigo);
            case 404:
                return new ApiException(Tipo.NO_ENCONTRADO,
                        valor(mensajeServidor, "No se encontró lo que se buscaba."), codigo);
            case 409:
                return new ApiException(Tipo.REGLA_NEGOCIO,
                        valor(mensajeServidor, "La operación no se puede realizar en este momento."), codigo);
            case 429:
                return new ApiException(Tipo.DEMASIADOS_INTENTOS,
                        valor(mensajeServidor, "Demasiados intentos. Inténtalo de nuevo más tarde."), codigo);
            default:
                return new ApiException(Tipo.ERROR,
                        valor(mensajeServidor, "La operación no pudo completarse (código " + codigo + ")."), codigo);
        }
    }

    /** Traduce una falla antes de tener respuesta (sin red, DNS, tiempo agotado...). */
    public static ApiException desdeFallo(Throwable t) {
        if (t instanceof ErrorDeAutenticacion) {
            return ((ErrorDeAutenticacion) t).causa;
        }
        if (t instanceof ApiException) {
            return (ApiException) t;
        }
        if (t instanceof IOException) {
            return new ApiException(Tipo.NO_DISPONIBLE, MENSAJE_NO_DISPONIBLE, 0, t);
        }
        return new ApiException(Tipo.ERROR, "Ocurrió un error inesperado al consultar los taxistas.", 0, t);
    }

    /**
     * Falla al iniciar sesión con la credencial de servicio, dentro del interceptor de OkHttp (que solo
     * puede lanzar {@link IOException}). {@link #desdeFallo} la desenvuelve para no perder el tipo.
     */
    static final class ErrorDeAutenticacion extends IOException {
        private static final long serialVersionUID = 1L;

        final transient ApiException causa;

        ErrorDeAutenticacion(ApiException causa) {
            super(causa.getMessage(), causa);
            this.causa = causa;
        }
    }

    private static String leerMensaje(Response<?> respuesta) {
        try {
            if (respuesta.errorBody() == null) {
                return null;
            }
            ErrorApi cuerpo = GSON.fromJson(respuesta.errorBody().string(), ErrorApi.class);
            return cuerpo == null ? null : cuerpo.getMensaje();
        } catch (Exception e) {
            // Si el cuerpo no es el JSON esperado se usa el texto genérico de cada código.
            return null;
        }
    }

    private static String valor(String mensaje, String porDefecto) {
        return mensaje == null || mensaje.trim().isEmpty() ? porDefecto : mensaje;
    }
}
