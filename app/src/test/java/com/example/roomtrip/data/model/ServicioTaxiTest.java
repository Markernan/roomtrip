package com.example.roomtrip.data.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/** Reglas del servicio de taxi que la app valida antes de escribir en Firestore. No necesitan emulador. */
public class ServicioTaxiTest {

    private static ServicioTaxi servicio(EstadoServicioTaxi estado, String taxistaId) {
        ServicioTaxi s = ServicioTaxi.nuevaSolicitud("cli-1", "Carlos", 4.9, "hot-1",
                "Hotel La Dolce Vita", "Aeropuerto Jorge Chávez", 8);
        s.setId("abc123");
        s.setEstado(estado);
        s.setTaxistaId(taxistaId);
        return s;
    }

    // ---- Exclusividad del pedido (arquitectura.md, regla 2) ----

    @Test
    public void nuevaSolicitud_quedaSolicitadoYSinTaxista() {
        ServicioTaxi s = ServicioTaxi.nuevaSolicitud(null, "Ana", 5.0, null, "Hotel", "Aeropuerto", 3);
        assertEquals(EstadoServicioTaxi.SOLICITADO, s.getEstado());
        assertNull(s.getTaxistaId());
        assertTrue(s.puedeSerAceptado());
    }

    @Test
    public void unPedidoYaTomadoNoSePuedeAceptar() {
        assertFalse(servicio(EstadoServicioTaxi.SOLICITADO, "otro-taxista").puedeSerAceptado());
        assertFalse(servicio(EstadoServicioTaxi.ASIGNADO, "otro-taxista").puedeSerAceptado());
    }

    // ---- RN-010: avance manual sin saltar estados ----

    @Test
    public void avanceManual_asignadoEnCaminoEnTraslado() {
        assertEquals(EstadoServicioTaxi.EN_CAMINO, servicio(EstadoServicioTaxi.ASIGNADO, "t1").siguienteEstadoManual());
        assertEquals(EstadoServicioTaxi.EN_TRASLADO, servicio(EstadoServicioTaxi.EN_CAMINO, "t1").siguienteEstadoManual());
    }

    @Test
    public void avanceManual_noAsignaNiFinaliza() {
        // ASIGNADO lo pone "aceptar pedido"; FINALIZADO solo el QR (RN-011)
        assertNull(servicio(EstadoServicioTaxi.SOLICITADO, null).siguienteEstadoManual());
        assertNull(servicio(EstadoServicioTaxi.EN_TRASLADO, "t1").siguienteEstadoManual());
        assertNull(servicio(EstadoServicioTaxi.FINALIZADO, "t1").siguienteEstadoManual());
        assertNull(servicio(null, "t1").siguienteEstadoManual());
    }

    @Test
    public void enCurso_soloEntreAsignadoYEnTraslado() {
        assertFalse(servicio(EstadoServicioTaxi.SOLICITADO, null).estaEnCurso());
        assertTrue(servicio(EstadoServicioTaxi.ASIGNADO, "t1").estaEnCurso());
        assertTrue(servicio(EstadoServicioTaxi.EN_CAMINO, "t1").estaEnCurso());
        assertTrue(servicio(EstadoServicioTaxi.EN_TRASLADO, "t1").estaEnCurso());
        assertFalse(servicio(EstadoServicioTaxi.FINALIZADO, "t1").estaEnCurso());
    }

    @Test
    public void esDelTaxista_comparaElUid() {
        assertTrue(servicio(EstadoServicioTaxi.ASIGNADO, "t1").esDelTaxista("t1"));
        assertFalse(servicio(EstadoServicioTaxi.ASIGNADO, "t1").esDelTaxista("t2"));
        assertFalse(servicio(EstadoServicioTaxi.SOLICITADO, null).esDelTaxista(null));
    }

    // ---- RN-011: cierre por QR ----

    @Test
    public void qr_validoSoloConElIdDelServicioYEnTraslado() {
        assertTrue(servicio(EstadoServicioTaxi.EN_TRASLADO, "t1").qrValido("abc123"));
        assertTrue(servicio(EstadoServicioTaxi.EN_TRASLADO, "t1").qrValido("  abc123\n"));
    }

    @Test
    public void qr_invalidoSiEsOtroCodigoOAunNoVaEnElTaxi() {
        assertFalse(servicio(EstadoServicioTaxi.EN_TRASLADO, "t1").qrValido("otro"));
        assertFalse(servicio(EstadoServicioTaxi.EN_TRASLADO, "t1").qrValido(null));
        assertFalse(servicio(EstadoServicioTaxi.EN_CAMINO, "t1").qrValido("abc123"));
    }
}
