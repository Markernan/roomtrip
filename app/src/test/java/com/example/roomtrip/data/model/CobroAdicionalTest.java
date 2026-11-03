package com.example.roomtrip.data.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.EnumSet;

/** RN-012 y escenario de prueba E10 del ERS: un cobro adicional exige monto, motivo y observación. */
public class CobroAdicionalTest {

    private static EnumSet<CobroAdicional.Error> validar(String motivo, String monto, String observacion) {
        return CobroAdicional.validar(motivo, monto, observacion);
    }

    @Test
    public void sinNadaEscritoNoHayCobroAdicionalYEsValido() {
        assertTrue(CobroAdicional.estaVacio("", "  ", null));
        assertTrue(validar("", "", "").isEmpty());
        assertTrue(validar(null, null, null).isEmpty());
    }

    @Test
    public void cobroCompletoEsValido() {
        assertTrue(validar("Televisor dañado", "150.00", "Pantalla con fisura").isEmpty());
    }

    @Test
    public void e10_sinMotivoSeRechaza() {
        assertEquals(EnumSet.of(CobroAdicional.Error.MOTIVO_OBLIGATORIO),
                validar("", "150", "Pantalla con fisura"));
    }

    @Test
    public void sinMontoSeRechaza() {
        assertEquals(EnumSet.of(CobroAdicional.Error.MONTO_OBLIGATORIO),
                validar("Televisor", "", "Pantalla con fisura"));
    }

    @Test
    public void sinObservacionSeRechaza() {
        assertEquals(EnumSet.of(CobroAdicional.Error.OBSERVACION_OBLIGATORIA),
                validar("Televisor", "150", "   "));
    }

    @Test
    public void conUnSoloCampoLlenoSeExigenLosOtrosDos() {
        assertEquals(EnumSet.of(CobroAdicional.Error.MONTO_OBLIGATORIO, CobroAdicional.Error.OBSERVACION_OBLIGATORIA),
                validar("Televisor", "", ""));
        assertEquals(EnumSet.of(CobroAdicional.Error.MOTIVO_OBLIGATORIO, CobroAdicional.Error.OBSERVACION_OBLIGATORIA),
                validar("", "150", ""));
    }

    @Test
    public void montoQueNoEsPositivoONoEsNumeroEsInvalido() {
        assertEquals(EnumSet.of(CobroAdicional.Error.MONTO_INVALIDO), validar("Televisor", "0", "Obs"));
        assertEquals(EnumSet.of(CobroAdicional.Error.MONTO_INVALIDO), validar("Televisor", "-20", "Obs"));
        assertEquals(EnumSet.of(CobroAdicional.Error.MONTO_INVALIDO), validar("Televisor", "abc", "Obs"));
        assertEquals(EnumSet.of(CobroAdicional.Error.MONTO_INVALIDO), validar("Televisor", "S/ 150", "Obs"));
        assertEquals(EnumSet.of(CobroAdicional.Error.MONTO_INVALIDO), validar("Televisor", "NaN", "Obs"));
    }

    @Test
    public void parsearMonto() {
        assertEquals(150.5, CobroAdicional.parsearMonto(" 150.50 "), 0.0);
        assertNull(CobroAdicional.parsearMonto("abc"));
        assertNull(CobroAdicional.parsearMonto(null));
        assertNull(CobroAdicional.parsearMonto("Infinity"));
    }
}
