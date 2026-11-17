package com.example.roomtrip.data.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * El repositorio contra un servidor simulado: autenticación automática, reutilización del token y
 * traducción de los errores (RF-TAX-018: "API no disponible" sin romper el resto de la app).
 */
public class TaxiRepositoryTest {

    private static final String LOGIN_OK = "{\"token\":\"%s\",\"tipo\":\"Bearer\",\"rol\":\"APP_MOVIL\","
            + "\"subject\":\"app\",\"expiraEnSegundos\":7200}";
    private static final String TAXISTA = "{\"id\":\"t1\",\"nombres\":\"Juan\",\"apellidos\":\"Pérez\","
            + "\"placa\":\"ABC-123\",\"estadoHabilitacion\":\"APROBADO\",\"estadoDisponibilidad\":\"DISPONIBLE\"}";

    private MockWebServer servidor;
    private TaxiRepository repo;
    private final List<ApiException> observados = new ArrayList<>();

    @Before
    public void preparar() throws Exception {
        servidor = new MockWebServer();
        servidor.start();
        repo = repositorio(15, 30);
    }

    @After
    public void cerrar() throws Exception {
        servidor.shutdown();
    }

    private TaxiRepository repositorio(long conectar, long leer) {
        return new TaxiRepository(
                new TaxiApiCliente(servidor.url("/").toString(), "app@x.pe", "clave-de-servicio", conectar, leer),
                observados::add);
    }

    private static MockResponse json(int codigo, String cuerpo) {
        return new MockResponse().setResponseCode(codigo).setHeader("Content-Type", "application/json").setBody(cuerpo);
    }

    /** Resultado de una llamada asíncrona, para esperarlo desde la prueba. */
    private static final class Resultado<T> {
        final CountDownLatch listo = new CountDownLatch(1);
        final AtomicReference<T> exito = new AtomicReference<>();
        final AtomicReference<Exception> fallo = new AtomicReference<>();

        T esperarExito() throws Exception {
            assertTrue("la llamada no terminó", listo.await(15, TimeUnit.SECONDS));
            assertNull("falló: " + fallo.get(), fallo.get());
            return exito.get();
        }

        ApiException esperarFallo() throws Exception {
            assertTrue("la llamada no terminó", listo.await(15, TimeUnit.SECONDS));
            assertNotNull("se esperaba un fallo", fallo.get());
            return (ApiException) fallo.get();
        }
    }

    private <T> Resultado<T> llamar(java.util.function.BiConsumer<com.example.roomtrip.data.firebase.Callbacks.Exito<T>,
            com.example.roomtrip.data.firebase.Callbacks.Fallo> operacion) {
        Resultado<T> r = new Resultado<>();
        operacion.accept(valor -> {
            r.exito.set(valor);
            r.listo.countDown();
        }, error -> {
            r.fallo.set(error);
            r.listo.countDown();
        });
        return r;
    }

    // ---------------------------------------------------------------- autenticación

    @Test
    public void iniciaSesionConLaCredencialDeServicioYUsaElTokenEnLaPeticion() throws Exception {
        servidor.enqueue(json(200, String.format(LOGIN_OK, "token-1")));
        servidor.enqueue(json(200, TAXISTA));

        TaxistaApi t = this.<TaxistaApi>llamar((ok, ko) -> repo.obtenerTaxista("t1", ok, ko)).esperarExito();

        assertEquals("ABC-123", t.getPlaca());
        RecordedRequest login = servidor.takeRequest();
        assertEquals("/api/auth/login", login.getPath());
        JsonObject cuerpoLogin = new Gson().fromJson(login.getBody().readUtf8(), JsonObject.class);
        assertEquals("app@x.pe", cuerpoLogin.get("correo").getAsString());
        assertEquals("clave-de-servicio", cuerpoLogin.get("password").getAsString());
        assertNull("el login no lleva token", login.getHeader("Authorization"));

        RecordedRequest datos = servidor.takeRequest();
        assertEquals("/api/taxistas/t1", datos.getPath());
        assertEquals("Bearer token-1", datos.getHeader("Authorization"));
    }

    @Test
    public void reutilizaElTokenEnLasPeticionesSiguientes() throws Exception {
        servidor.enqueue(json(200, String.format(LOGIN_OK, "token-1")));
        servidor.enqueue(json(200, TAXISTA));
        servidor.enqueue(json(200, TAXISTA));

        this.<TaxistaApi>llamar((ok, ko) -> repo.obtenerTaxista("t1", ok, ko)).esperarExito();
        this.<TaxistaApi>llamar((ok, ko) -> repo.obtenerTaxista("t1", ok, ko)).esperarExito();

        assertEquals("un login y dos consultas", 3, servidor.getRequestCount());
    }

    @Test
    public void siElTokenVencioRenuevaLaSesionYReintentaUnaVez() throws Exception {
        servidor.enqueue(json(200, String.format(LOGIN_OK, "token-viejo")));
        servidor.enqueue(json(401, "{\"error\":\"NO_AUTORIZADO\"}"));
        servidor.enqueue(json(200, String.format(LOGIN_OK, "token-nuevo")));
        servidor.enqueue(json(200, TAXISTA));

        TaxistaApi t = this.<TaxistaApi>llamar((ok, ko) -> repo.obtenerTaxista("t1", ok, ko)).esperarExito();

        assertEquals("t1", t.getId());
        servidor.takeRequest();                                  // login con el token viejo
        assertEquals("Bearer token-viejo", servidor.takeRequest().getHeader("Authorization"));
        servidor.takeRequest();                                  // segundo login
        assertEquals("Bearer token-nuevo", servidor.takeRequest().getHeader("Authorization"));
    }

    @Test
    public void unaCredencialDeServicioInvalidaEsUnErrorDeAutenticacionNoDeRed() throws Exception {
        servidor.enqueue(json(401, "{\"error\":\"NO_AUTORIZADO\",\"mensaje\":\"Correo o contrasenia incorrectos\"}"));

        ApiException e = this.<TaxistaApi>llamar((ok, ko) -> repo.obtenerTaxista("t1", ok, ko)).esperarFallo();

        assertEquals(ApiException.Tipo.NO_AUTENTICADO, e.getTipo());
        assertFalse(e.esNoDisponible());
    }

    @Test
    public void elBloqueoPorDemasiadosIntentosSeReportaComoTal() throws Exception {
        servidor.enqueue(json(429, "{\"error\":\"DEMASIADOS_INTENTOS\","
                + "\"mensaje\":\"Demasiados intentos fallidos. Intenta de nuevo en 15 minuto(s).\"}"));

        ApiException e = this.<TaxistaApi>llamar((ok, ko) -> repo.obtenerTaxista("t1", ok, ko)).esperarFallo();

        assertEquals(ApiException.Tipo.DEMASIADOS_INTENTOS, e.getTipo());
        assertTrue(e.getMessage().contains("15 minuto"));
    }

    // ---------------------------------------------------------------- operaciones

    @Test
    public void listaLosTaxistasDisponibles() throws Exception {
        servidor.enqueue(json(200, String.format(LOGIN_OK, "t")));
        servidor.enqueue(json(200, "[" + TAXISTA + "," + TAXISTA.replace("t1", "t2") + "]"));

        List<TaxistaApi> lista = this.<List<TaxistaApi>>llamar((ok, ko) -> repo.taxistasDisponibles(ok, ko)).esperarExito();

        assertEquals(2, lista.size());
        assertEquals("t2", lista.get(1).getId());
        servidor.takeRequest();
        assertEquals("/api/taxistas/disponibles", servidor.takeRequest().getPath());
    }

    @Test
    public void buscaAlTaxistaPorCorreoCodificandoElParametro() throws Exception {
        servidor.enqueue(json(200, String.format(LOGIN_OK, "t")));
        servidor.enqueue(json(200, TAXISTA));

        this.<TaxistaApi>llamar((ok, ko) -> repo.buscarPorCorreo("juan+taxi@x.pe", ok, ko)).esperarExito();

        servidor.takeRequest();
        RecordedRequest r = servidor.takeRequest();
        assertEquals("GET", r.getMethod());
        assertEquals("/api/taxistas/buscar?correo=juan%2Btaxi%40x.pe", r.getPath());
    }

    @Test
    public void unCorreoSinTaxistaResponde404ConElMensajeDelServidor() throws Exception {
        servidor.enqueue(json(200, String.format(LOGIN_OK, "t")));
        servidor.enqueue(json(404, "{\"status\":404,\"error\":\"NO_ENCONTRADO\","
                + "\"mensaje\":\"No hay un taxista registrado con ese correo\"}"));

        ApiException e = this.<TaxistaApi>llamar((ok, ko) -> repo.buscarPorCorreo("nadie@x.pe", ok, ko)).esperarFallo();

        assertEquals(ApiException.Tipo.NO_ENCONTRADO, e.getTipo());
        assertEquals("No hay un taxista registrado con ese correo", e.getMessage());
    }

    @Test
    public void cambiaLaDisponibilidadConUnPatch() throws Exception {
        servidor.enqueue(json(200, String.format(LOGIN_OK, "t")));
        servidor.enqueue(json(200, TAXISTA.replace("DISPONIBLE", "EN_SERVICIO")));

        TaxistaApi t = this.<TaxistaApi>llamar((ok, ko) ->
                repo.cambiarDisponibilidad("t1", TaxistaApi.EN_SERVICIO, ok, ko)).esperarExito();

        assertEquals("EN_SERVICIO", t.getEstadoDisponibilidad());
        servidor.takeRequest();
        RecordedRequest r = servidor.takeRequest();
        assertEquals("PATCH", r.getMethod());
        assertEquals("/api/taxistas/t1/disponibilidad", r.getPath());
        assertEquals("EN_SERVICIO", new Gson().fromJson(r.getBody().readUtf8(), JsonObject.class).get("estado").getAsString());
    }

    @Test
    public void calificaAlTaxistaYUnaSegundaCalificacionDelMismoServicioEsReglaDeNegocio() throws Exception {
        servidor.enqueue(json(200, String.format(LOGIN_OK, "t")));
        servidor.enqueue(json(201, "{\"id\":\"c1\",\"taxistaId\":\"t1\",\"servicioTaxiId\":\"s1\",\"puntuacion\":5}"));
        servidor.enqueue(json(409, "{\"status\":409,\"error\":\"REGLA_NEGOCIO\","
                + "\"mensaje\":\"Ese servicio de taxi ya fue calificado\"}"));

        CalificacionApi c = this.<CalificacionApi>llamar((ok, ko) ->
                repo.calificar("t1", "s1", 5, "Muy amable", ok, ko)).esperarExito();
        assertEquals("c1", c.getId());

        servidor.takeRequest();
        JsonObject cuerpo = new Gson().fromJson(servidor.takeRequest().getBody().readUtf8(), JsonObject.class);
        assertEquals("t1", cuerpo.get("taxistaId").getAsString());
        assertEquals("s1", cuerpo.get("servicioTaxiId").getAsString());
        assertEquals(5, cuerpo.get("puntuacion").getAsInt());
        assertEquals("Muy amable", cuerpo.get("observacion").getAsString());

        ApiException repetida = this.<CalificacionApi>llamar((ok, ko) ->
                repo.calificar("t1", "s1", 5, null, ok, ko)).esperarFallo();
        assertEquals(ApiException.Tipo.REGLA_NEGOCIO, repetida.getTipo());
        assertEquals("Ese servicio de taxi ya fue calificado", repetida.getMessage());
    }

    @Test
    public void leeLaValoracionPromedio() throws Exception {
        servidor.enqueue(json(200, String.format(LOGIN_OK, "t")));
        servidor.enqueue(json(200, "{\"taxistaId\":\"t1\",\"promedio\":4.33,\"totalCalificaciones\":3}"));

        ValoracionApi v = this.<ValoracionApi>llamar((ok, ko) -> repo.valoracion("t1", ok, ko)).esperarExito();

        assertEquals(4.33, v.getPromedio(), 0.0001);
        assertEquals(3, v.getTotalCalificaciones());
    }

    // ---------------------------------------------------------------- RF-TAX-018: API no disponible

    @Test
    public void conElCircuitoAbiertoDelGatewayLaAppRecibeElAvisoDeNoDisponible() throws Exception {
        servidor.enqueue(json(200, String.format(LOGIN_OK, "t")));
        servidor.enqueue(json(503, "{\"status\":503,\"error\":\"SERVICIO_NO_DISPONIBLE\","
                + "\"servicio\":\"taxistas\",\"mensaje\":\"El servicio de gestion de taxistas no esta disponible.\"}"));

        ApiException e = this.<List<TaxistaApi>>llamar((ok, ko) -> repo.taxistasDisponibles(ok, ko)).esperarFallo();

        assertTrue(e.esNoDisponible());
        assertEquals(ApiException.MENSAJE_NO_DISPONIBLE, e.getMessage());
        assertEquals("el observador recibe el fallo para registrarlo", 1, observados.size());
        assertTrue(observados.get(0).esNoDisponible());
    }

    @Test
    public void unError500TrasUnRatoSinUsoTambienSeTrataComoNoDisponible() throws Exception {
        servidor.enqueue(json(200, String.format(LOGIN_OK, "t")));
        servidor.enqueue(json(500, "{}"));

        ApiException e = this.<TaxistaApi>llamar((ok, ko) -> repo.obtenerTaxista("t1", ok, ko)).esperarFallo();

        assertTrue(e.esNoDisponible());
    }

    @Test
    public void sinConexionLaAppRecibeElAvisoEnLugarDeUnaExcepcionCruda() throws Exception {
        servidor.shutdown();

        ApiException e = this.<TaxistaApi>llamar((ok, ko) -> repo.obtenerTaxista("t1", ok, ko)).esperarFallo();

        assertTrue(e.esNoDisponible());
        assertEquals(ApiException.MENSAJE_NO_DISPONIBLE, e.getMessage());
    }

    @Test
    public void siElServicioNoRespondeATiempoElTiempoAgotadoEsNoDisponible() throws Exception {
        repo = repositorio(5, 1);
        servidor.enqueue(json(200, String.format(LOGIN_OK, "t")));
        servidor.enqueue(json(200, TAXISTA).setBodyDelay(4, TimeUnit.SECONDS));

        ApiException e = this.<TaxistaApi>llamar((ok, ko) -> repo.obtenerTaxista("t1", ok, ko)).esperarFallo();

        assertTrue(e.esNoDisponible());
    }

    // ---------------------------------------------------------------- configuración

    @Test
    public void sinCredencialNoSaleALaRedYAvisaQueFaltaConfigurar() throws Exception {
        TaxiRepository sinConfigurar = new TaxiRepository(
                new TaxiApiCliente(servidor.url("/").toString(), "", ""), observados::add);

        Resultado<TaxistaApi> r = new Resultado<>();
        sinConfigurar.obtenerTaxista("t1", v -> r.listo.countDown(), e -> {
            r.fallo.set(e);
            r.listo.countDown();
        });

        ApiException e = r.esperarFallo();
        assertEquals(ApiException.Tipo.NO_CONFIGURADA, e.getTipo());
        assertEquals("no debe haber ninguna petición", 0, servidor.getRequestCount());
    }
}
