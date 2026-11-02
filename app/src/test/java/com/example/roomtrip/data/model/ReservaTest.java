package com.example.roomtrip.data.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;

/** Reglas de fechas y estados de la reserva (RN-001, RN-002, RN-005, RN-006). No necesitan emulador. */
public class ReservaTest {

    private static Reserva reserva(String entrada, String salida, EstadoReserva estado, EstadoCheckout checkout) {
        Reserva r = new Reserva();
        r.setFechaEntrada(entrada);
        r.setFechaSalida(salida);
        r.setEstado(estado);
        r.setEstadoCheckout(checkout);
        r.setMontoTotal(300);
        return r;
    }

    @Test
    public void noches_noIncluyenElDiaDeSalida() {
        assertEquals(Arrays.asList("2026-11-10", "2026-11-11"), Reserva.nochesEntre("2026-11-10", "2026-11-12"));
        assertEquals(2, Reserva.contarNoches("2026-11-10", "2026-11-12"));
    }

    @Test
    public void noches_cruzanFinDeMes() {
        assertEquals(Arrays.asList("2026-11-30", "2026-12-01"), Reserva.nochesEntre("2026-11-30", "2026-12-02"));
    }

    @Test
    public void noches_rangoInvalidoDevuelveVacio() {
        assertEquals(0, Reserva.contarNoches("2026-11-12", "2026-11-10"));
        assertEquals(0, Reserva.contarNoches("2026-11-10", "2026-11-10"));
        assertEquals(0, Reserva.contarNoches(null, "2026-11-10"));
        assertEquals(0, Reserva.contarNoches("10/11/2026", "2026-11-12"));
    }

    @Test
    public void superposicion_compartirUnaNocheCruza() {
        assertTrue(Reserva.seSuperponen("2026-11-10", "2026-11-13", "2026-11-12", "2026-11-15"));
        assertTrue(Reserva.seSuperponen("2026-11-10", "2026-11-20", "2026-11-12", "2026-11-13"));
    }

    @Test
    public void superposicion_salidaYEntradaElMismoDiaNoCruza() {
        // Uno sale el 12 y otro entra el 12: la noche del 12 es solo del segundo
        assertFalse(Reserva.seSuperponen("2026-11-10", "2026-11-12", "2026-11-12", "2026-11-14"));
    }

    @Test
    public void checkout_soloConReservaActivaYaIniciadaYSinCheckoutPrevio() {
        LocalDate hoy = LocalDate.parse("2026-11-11");
        assertTrue(reserva("2026-11-10", "2026-11-12", EstadoReserva.CONFIRMADA, null).puedeHacerCheckout(hoy));
        assertTrue(reserva("2026-11-11", "2026-11-12", EstadoReserva.CONFIRMADA, null).puedeHacerCheckout(hoy));
        // Todavía no empieza
        assertFalse(reserva("2026-11-12", "2026-11-14", EstadoReserva.CONFIRMADA, null).puedeHacerCheckout(hoy));
        // Ya lo pidió
        assertFalse(reserva("2026-11-10", "2026-11-12", EstadoReserva.EN_CURSO, EstadoCheckout.PENDIENTE).puedeHacerCheckout(hoy));
        // Cancelada o finalizada
        assertFalse(reserva("2026-11-10", "2026-11-12", EstadoReserva.CANCELADA, null).puedeHacerCheckout(hoy));
        assertFalse(reserva("2026-11-10", "2026-11-12", EstadoReserva.FINALIZADA, EstadoCheckout.PROCESADO).puedeHacerCheckout(hoy));
    }

    @Test
    public void chat_desdeLaConfirmacionHastaQueElHotelCobra() {
        assertTrue(reserva("2026-11-10", "2026-11-12", EstadoReserva.CONFIRMADA, null).chatDisponible());
        assertTrue(reserva("2026-11-10", "2026-11-12", EstadoReserva.EN_CURSO, EstadoCheckout.PENDIENTE).chatDisponible());
        assertFalse(reserva("2026-11-10", "2026-11-12", EstadoReserva.FINALIZADA, EstadoCheckout.PROCESADO).chatDisponible());
        assertFalse(reserva("2026-11-10", "2026-11-12", EstadoReserva.CANCELADA, null).chatDisponible());
    }

    @Test
    public void taxiGratis_siAlcanzaElMontoMinimoDelHotel() {
        Reserva r = reserva("2026-11-10", "2026-11-12", EstadoReserva.CONFIRMADA, null);
        assertTrue(r.calificaParaTaxi(300));
        assertTrue(r.calificaParaTaxi(200));
        assertFalse(r.calificaParaTaxi(300.01));
    }

    @Test
    public void nueva_calculaNochesYTotal() {
        Hotel hotel = new Hotel();
        hotel.setId("h1");
        hotel.setNombre("Hotel");
        Habitacion hab = new Habitacion("Suite", "2 Adultos", 150.0, EstadoHabitacion.DISPONIBLE);
        hab.setId("hab1");
        Reserva r = Reserva.nueva("c1", "Ana", hotel, hab, "2026-11-10", "2026-11-13", "4242");
        assertEquals(3, r.getNoches());
        assertEquals(450.0, r.getMontoTotal(), 0.001);
        assertEquals(EstadoReserva.CONFIRMADA, r.getEstado());
        assertEquals("hab1", r.getHabitacionId());
    }
}
