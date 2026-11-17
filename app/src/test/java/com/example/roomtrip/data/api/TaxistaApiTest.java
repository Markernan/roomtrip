package com.example.roomtrip.data.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;

import org.junit.Test;

/** Que el JSON real de la API se convierta bien al modelo de la app. */
public class TaxistaApiTest {

    /** Respuesta de la vista reducida de la API, más un campo que la app no conoce. */
    private static final String JSON = "{\"id\":\"66f1c0\",\"nombres\":\"Juan\",\"apellidos\":\"Pérez\","
            + "\"correo\":\"juan@x.pe\",\"telefono\":\"987654321\",\"fotoUrl\":\"https://x/f.jpg\","
            + "\"vehiculoMarca\":\"Toyota\",\"vehiculoModelo\":\"Yaris\",\"vehiculoAnio\":\"2020\","
            + "\"vehiculoColor\":\"Blanco\",\"placa\":\"ABC-123\",\"fotoVehiculoUrl\":\"https://x/v.jpg\","
            + "\"estadoHabilitacion\":\"APROBADO\",\"estadoDisponibilidad\":\"DISPONIBLE\","
            + "\"valoracionPromedio\":4.8,\"totalCalificaciones\":14,\"campoNuevoQueLaAppNoConoce\":true}";

    private final TaxistaApi taxista = new Gson().fromJson(JSON, TaxistaApi.class);

    @Test
    public void leeLosCamposDeLaApi() {
        assertEquals("66f1c0", taxista.getId());
        assertEquals("ABC-123", taxista.getPlaca());
        assertEquals(4.8, taxista.getValoracionPromedio(), 0.0001);
        assertEquals(14, taxista.getTotalCalificaciones());
        assertEquals("https://x/f.jpg", taxista.getFotoUrl());
    }

    @Test
    public void armaLosTextosParaMostrar() {
        assertEquals("Juan Pérez", taxista.nombreCompleto());
        assertEquals("Toyota Yaris 2020 · Blanco", taxista.descripcionVehiculo());
    }

    @Test
    public void tolerarCamposVaciosAlArmarLosTextos() {
        TaxistaApi vacio = new Gson().fromJson("{\"nombres\":\"Ana\"}", TaxistaApi.class);

        assertEquals("Ana", vacio.nombreCompleto());
        assertEquals("", vacio.descripcionVehiculo());
        assertFalse(vacio.estaAprobado());
    }

    @Test
    public void soloUnTaxistaAprobadoYDisponibleEstaDisponible() {
        assertTrue(taxista.estaAprobado());
        assertTrue(taxista.estaDisponible());

        TaxistaApi inhabilitado = new Gson().fromJson(
                "{\"estadoHabilitacion\":\"INHABILITADO\",\"estadoDisponibilidad\":\"DISPONIBLE\"}", TaxistaApi.class);
        assertFalse(inhabilitado.estaAprobado());
        assertFalse(inhabilitado.estaDisponible());
    }
}
