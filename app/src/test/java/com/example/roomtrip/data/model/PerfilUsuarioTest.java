package com.example.roomtrip.data.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Arrays;

/** Quién puede entrar a la app según su rol, estado y aprobación. No necesita emulador. */
public class PerfilUsuarioTest {

    private static PerfilUsuario aprobado() {
        PerfilUsuario p = PerfilUsuario.nuevaSolicitudTaxista("Juan", "juan@pucp.edu.pe");
        p.setEstadoAprobacion(EstadoAprobacion.APROBADO);
        return p;
    }

    @Test
    public void nuevaSolicitud_quedaPendienteYNoPuedeOperar() {
        PerfilUsuario p = PerfilUsuario.nuevaSolicitudTaxista("Juan", "juan@pucp.edu.pe");
        assertEquals(RolUsuario.TAXISTA, p.getRol());
        assertEquals(EstadoCuenta.ACTIVO, p.getEstado());
        assertEquals(EstadoAprobacion.PENDIENTE, p.getEstadoAprobacion());
        assertFalse(p.puedeOperar());
    }

    @Test
    public void aprobadoYActivo_puedeOperar() {
        assertTrue(aprobado().puedeOperar());
    }

    @Test
    public void desactivadoPorElSuperadmin_noPuedeOperar() {
        PerfilUsuario p = aprobado();
        p.setEstado(EstadoCuenta.INACTIVO);
        assertFalse(p.puedeOperar());
    }

    @Test
    public void rechazadoOConOtroRol_noPuedeOperar() {
        PerfilUsuario rechazado = aprobado();
        rechazado.setEstadoAprobacion(EstadoAprobacion.RECHAZADO);
        assertFalse(rechazado.puedeOperar());

        // La aprobación es solo para taxistas: un cliente desactivado tampoco opera
        PerfilUsuario cliente = aprobado();
        cliente.setRol(RolUsuario.CLIENTE);
        cliente.setEstado(EstadoCuenta.INACTIVO);
        assertFalse(cliente.puedeOperar());
    }

    @Test
    public void documentoIncompleto_noPuedeOperar() {
        // Un documento creado a mano en la consola sin estos campos no debe dejar entrar
        assertFalse(new PerfilUsuario().puedeOperar());
    }

    @Test
    public void cliente_quedaActivoYPuedeOperarDeInmediato() {
        PerfilUsuario c = PerfilUsuario.nuevoCliente("Ana María", "Gómez", "DNI", "12345678",
                "01/01/2000", "ana@gmail.com", "999888777", "Av. Larco 1");
        assertEquals(RolUsuario.CLIENTE, c.getRol());
        assertTrue(c.puedeOperar());
        assertEquals("Ana", c.primerNombre());
    }

    @Test
    public void adminDeHotel_necesitaHotelAsignado() {
        assertTrue(PerfilUsuario.nuevoAdminHotel("Luis", "luis@hotel.pe", "h1", "Hotel").puedeOperar());
        assertFalse(PerfilUsuario.nuevoAdminHotel("Luis", "luis@hotel.pe", null, null).puedeOperar());
        assertFalse(PerfilUsuario.nuevoAdminHotel("Luis", "luis@hotel.pe", "", "").puedeOperar());
    }

    @Test
    public void nombreCompleto_sinEspaciosDeSobra() {
        PerfilUsuario p = aprobado();
        assertEquals("Juan", p.nombreCompleto());
        p.setApellidos("Pérez");
        assertEquals("Juan Pérez", p.nombreCompleto());
        p.setNombres(null);
        assertEquals("Pérez", p.nombreCompleto());
    }

    @Test
    public void losNombresDeLosEnumsCoincidenConLasReglasYLaApi() {
        // firestore.rules compara contra estos textos y la API de taxistas usa los mismos
        assertEquals(Arrays.asList("CLIENTE", "ADMIN_HOTEL", "TAXISTA", "SUPERADMIN"),
                Arrays.asList(nombres(RolUsuario.values())));
        assertEquals(Arrays.asList("PENDIENTE", "APROBADO", "RECHAZADO"),
                Arrays.asList(nombres(EstadoAprobacion.values())));
    }

    private static String[] nombres(Enum<?>[] valores) {
        String[] r = new String[valores.length];
        for (int i = 0; i < valores.length; i++) r[i] = valores[i].name();
        return r;
    }
}
