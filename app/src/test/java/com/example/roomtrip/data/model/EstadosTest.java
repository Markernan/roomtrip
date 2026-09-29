package com.example.roomtrip.data.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Arrays;

/** Reglas del ERS que codifican los enums de estado. No necesitan emulador. */
public class EstadosTest {

    // ---- EstadoServicioTaxi: RN-010 / RF-TAX-008 ----

    @Test
    public void taxi_secuenciaCompletaEnElOrdenDelERS() {
        assertEquals(EstadoServicioTaxi.ASIGNADO, EstadoServicioTaxi.SOLICITADO.siguiente());
        assertEquals(EstadoServicioTaxi.EN_CAMINO, EstadoServicioTaxi.ASIGNADO.siguiente());
        assertEquals(EstadoServicioTaxi.EN_TRASLADO, EstadoServicioTaxi.EN_CAMINO.siguiente());
        assertEquals(EstadoServicioTaxi.FINALIZADO, EstadoServicioTaxi.EN_TRASLADO.siguiente());
        assertNull(EstadoServicioTaxi.FINALIZADO.siguiente());
    }

    @Test
    public void taxi_noSePermiteSaltarEstados() {
        assertFalse(EstadoServicioTaxi.SOLICITADO.puedePasarA(EstadoServicioTaxi.EN_CAMINO));
        assertFalse(EstadoServicioTaxi.ASIGNADO.puedePasarA(EstadoServicioTaxi.FINALIZADO));
    }

    @Test
    public void taxi_noSePermiteRetroceder_niSalirDeFinalizado() {
        assertFalse(EstadoServicioTaxi.EN_TRASLADO.puedePasarA(EstadoServicioTaxi.ASIGNADO));
        assertFalse(EstadoServicioTaxi.FINALIZADO.puedePasarA(EstadoServicioTaxi.SOLICITADO));
        assertFalse(EstadoServicioTaxi.ASIGNADO.puedePasarA(EstadoServicioTaxi.ASIGNADO));
        assertFalse(EstadoServicioTaxi.SOLICITADO.puedePasarA(null));
    }

    @Test
    public void taxi_unPasoAdelanteSiEsValido() {
        assertTrue(EstadoServicioTaxi.SOLICITADO.puedePasarA(EstadoServicioTaxi.ASIGNADO));
        assertTrue(EstadoServicioTaxi.EN_TRASLADO.puedePasarA(EstadoServicioTaxi.FINALIZADO));
    }

    @Test
    public void taxi_losNombresCoincidenConLaApiYElReadme() {
        // Si alguien renombra o reordena un valor, esto falla: Firestore y la API guardan el nombre.
        assertEquals(
                Arrays.asList("SOLICITADO", "ASIGNADO", "EN_CAMINO", "EN_TRASLADO", "FINALIZADO"),
                Arrays.asList(nombres(EstadoServicioTaxi.values())));
        assertEquals(EstadoServicioTaxi.EN_CAMINO, EstadoServicioTaxi.valueOf("EN_CAMINO"));
        assertEquals("En camino", EstadoServicioTaxi.EN_CAMINO.getEtiqueta());
    }

    // ---- EstadoReserva: RF-RES-010, glosario del ERS, RF-CHK-001 ----

    @Test
    public void reserva_tieneLosCincoEstadosDelERS() {
        assertEquals(
                Arrays.asList("PENDIENTE", "CONFIRMADA", "EN_CURSO", "FINALIZADA", "CANCELADA"),
                Arrays.asList(nombres(EstadoReserva.values())));
    }

    @Test
    public void reserva_activaEsSoloConfirmadaOEnCurso() {
        assertFalse(EstadoReserva.PENDIENTE.esActiva());
        assertTrue(EstadoReserva.CONFIRMADA.esActiva());
        assertTrue(EstadoReserva.EN_CURSO.esActiva());
        assertFalse(EstadoReserva.FINALIZADA.esActiva());
        assertFalse(EstadoReserva.CANCELADA.esActiva());
    }

    @Test
    public void reserva_checkoutSoloSiEstaEnCurso() {
        for (EstadoReserva estado : EstadoReserva.values()) {
            assertEquals(estado.name(), estado == EstadoReserva.EN_CURSO, estado.permiteCheckout());
        }
    }

    // ---- EstadoCheckout y EstadoHabitacion: formalizan lo que muestra la UI (el ERS no los define) ----

    @Test
    public void checkout_soloProcesadoCuentaComoProcesado() {
        assertEquals(Arrays.asList("PENDIENTE", "PROCESADO"), Arrays.asList(nombres(EstadoCheckout.values())));
        assertFalse(EstadoCheckout.PENDIENTE.estaProcesado());
        assertTrue(EstadoCheckout.PROCESADO.estaProcesado());
        assertEquals("Procesado", EstadoCheckout.PROCESADO.getEtiqueta());
    }

    @Test
    public void habitacion_losTresEstadosYSusEtiquetas() {
        assertEquals(Arrays.asList("DISPONIBLE", "OCUPADA", "MANTENIMIENTO"),
                Arrays.asList(nombres(EstadoHabitacion.values())));
        assertEquals("Mantenimiento", EstadoHabitacion.MANTENIMIENTO.getEtiqueta());
    }

    // ---- EstadoCuenta y Hotel: RF-USR-007 ----

    @Test
    public void cuenta_desdeElValorDeUnSwitch() {
        assertEquals(EstadoCuenta.ACTIVO, EstadoCuenta.desde(true));
        assertEquals(EstadoCuenta.INACTIVO, EstadoCuenta.desde(false));
        assertTrue(EstadoCuenta.ACTIVO.esActivo());
        assertFalse(EstadoCuenta.INACTIVO.esActivo());
    }

    @Test
    public void hotel_delCliente_naceActivoPorDefecto() {
        Hotel hotel = new Hotel("Hotel Miraflores", "Lima", "S/250 noche", 4.8f, 0);
        assertEquals(EstadoCuenta.ACTIVO, hotel.getEstado());
    }

    @Test
    public void hotel_deSuperadmin_respetaElFlagActivo() {
        Hotel inactivo = new Hotel("H", "U", 4f, "A", "a@a.com", "1", 0, false);
        Hotel activo = new Hotel("H", "U", 4f, "A", "a@a.com", "1", 0, true);
        assertEquals(EstadoCuenta.INACTIVO, inactivo.getEstado());
        assertEquals(EstadoCuenta.ACTIVO, activo.getEstado());
    }

    private static String[] nombres(Enum<?>[] valores) {
        String[] nombres = new String[valores.length];
        for (int i = 0; i < valores.length; i++) nombres[i] = valores[i].name();
        return nombres;
    }
}
