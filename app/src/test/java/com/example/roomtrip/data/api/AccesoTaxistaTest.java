package com.example.roomtrip.data.api;

import static org.junit.Assert.assertEquals;

import com.example.roomtrip.data.api.AccesoTaxista.Resultado;
import com.google.gson.Gson;

import org.junit.Test;

/** La API manda: solo un taxista APROBADO en el portal puede entrar a la app. */
public class AccesoTaxistaTest {

    private static TaxistaApi conEstado(String estado) {
        return new Gson().fromJson("{\"estadoHabilitacion\":\"" + estado + "\"}", TaxistaApi.class);
    }

    @Test
    public void soloElAprobadoEntra() {
        assertEquals(Resultado.PERMITIDO, AccesoTaxista.desdeTaxista(conEstado("APROBADO")));
    }

    @Test
    public void elPendienteEsperaLaAprobacion() {
        assertEquals(Resultado.PENDIENTE, AccesoTaxista.desdeTaxista(conEstado("PENDIENTE")));
    }

    @Test
    public void elRechazadoOInhabilitadoNoEntra() {
        assertEquals(Resultado.RECHAZADO, AccesoTaxista.desdeTaxista(conEstado("RECHAZADO")));
        assertEquals(Resultado.RECHAZADO, AccesoTaxista.desdeTaxista(conEstado("INHABILITADO")));
    }

    @Test
    public void unEstadoDesconocidoNuncaDejaPasar() {
        assertEquals(Resultado.RECHAZADO, AccesoTaxista.desdeTaxista(conEstado("ALGO_NUEVO")));
        assertEquals(Resultado.RECHAZADO, AccesoTaxista.desdeTaxista(new TaxistaApi()));
    }

    @Test
    public void sinTaxistaEsNoRegistrado() {
        assertEquals(Resultado.NO_REGISTRADO, AccesoTaxista.desdeTaxista(null));
    }

    @Test
    public void traduceLosErroresDeLaApi() {
        assertEquals(Resultado.NO_REGISTRADO, error(ApiException.Tipo.NO_ENCONTRADO));
        assertEquals(Resultado.SERVICIO_NO_DISPONIBLE, error(ApiException.Tipo.NO_DISPONIBLE));
        assertEquals(Resultado.SERVICIO_NO_DISPONIBLE, error(ApiException.Tipo.DEMASIADOS_INTENTOS));
        assertEquals(Resultado.SIN_CONFIGURAR, error(ApiException.Tipo.NO_CONFIGURADA));
        assertEquals(Resultado.ERROR, error(ApiException.Tipo.NO_AUTENTICADO));
        assertEquals(Resultado.ERROR, error(ApiException.Tipo.SIN_PERMISO));
    }

    private static Resultado error(ApiException.Tipo tipo) {
        return AccesoTaxista.desdeError(new ApiException(tipo, "x", 0));
    }
}
