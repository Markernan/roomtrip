package com.example.roomtrip.data.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.SocketTimeoutException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;

import org.junit.Test;

/** Cada código HTTP debe convertirse en el tipo de error y el mensaje correctos para el usuario. */
public class ApiExceptionTest {

    private static ApiException con(int codigo, String json) {
        Response<Object> respuesta = Response.error(codigo,
                ResponseBody.create(MediaType.parse("application/json"), json));
        return ApiException.desdeRespuesta(respuesta);
    }

    @Test
    public void todoErrorDelServidorEsNoDisponibleConElMensajeDelRequisito() {
        for (int codigo : new int[] {500, 502, 503, 504}) {
            ApiException e = con(codigo, "{\"error\":\"SERVICIO_NO_DISPONIBLE\",\"mensaje\":\"x\"}");
            assertEquals("codigo " + codigo, ApiException.Tipo.NO_DISPONIBLE, e.getTipo());
            assertTrue(e.esNoDisponible());
            assertEquals(ApiException.MENSAJE_NO_DISPONIBLE, e.getMessage());
            assertEquals(codigo, e.getCodigoHttp());
        }
    }

    @Test
    public void unaReglaDeNegocioConservaElMensajeDelServidor() {
        ApiException e = con(409, "{\"status\":409,\"error\":\"REGLA_NEGOCIO\","
                + "\"mensaje\":\"Ese servicio de taxi ya fue calificado\"}");

        assertEquals(ApiException.Tipo.REGLA_NEGOCIO, e.getTipo());
        assertEquals("Ese servicio de taxi ya fue calificado", e.getMessage());
        assertFalse(e.esNoDisponible());
    }

    @Test
    public void asignaElTipoSegunElCodigo() {
        assertEquals(ApiException.Tipo.DATOS_INVALIDOS, con(400, "{}").getTipo());
        assertEquals(ApiException.Tipo.NO_AUTENTICADO, con(401, "{}").getTipo());
        assertEquals(ApiException.Tipo.SIN_PERMISO, con(403, "{}").getTipo());
        assertEquals(ApiException.Tipo.NO_ENCONTRADO, con(404, "{}").getTipo());
        assertEquals(ApiException.Tipo.DEMASIADOS_INTENTOS, con(429, "{}").getTipo());
        assertEquals(ApiException.Tipo.ERROR, con(418, "{}").getTipo());
    }

    @Test
    public void unCuerpoQueNoEsJsonUsaElTextoGenerico() {
        ApiException e = con(404, "<html>no soy json</html>");

        assertEquals(ApiException.Tipo.NO_ENCONTRADO, e.getTipo());
        assertEquals("No se encontró lo que se buscaba.", e.getMessage());
    }

    @Test
    public void sinRedOTiempoAgotadoEsNoDisponible() {
        assertEquals(ApiException.Tipo.NO_DISPONIBLE, ApiException.desdeFallo(new IOException("sin red")).getTipo());
        assertEquals(ApiException.Tipo.NO_DISPONIBLE,
                ApiException.desdeFallo(new SocketTimeoutException("timeout")).getTipo());
    }

    @Test
    public void unaFallaQueNoEsDeRedEsErrorGenerico() {
        ApiException e = ApiException.desdeFallo(new IllegalStateException("bug"));

        assertEquals(ApiException.Tipo.ERROR, e.getTipo());
        assertFalse(e.esNoDisponible());
    }
}
