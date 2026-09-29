package com.example.roomtrip.data.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ValoracionTest {

    private static String iniciales(String usuario) {
        return new Valoracion(usuario, "", "", 5f, "").getInicialesUsuario();
    }

    @Test
    public void inicialesDeNombreYApellido() {
        assertEquals("MF", iniciales("María Fernández"));
        assertEquals("CR", iniciales("carlos ruiz"));
    }

    @Test
    public void conUnaSolaPalabraEsSuInicial() {
        assertEquals("J", iniciales("Juan"));
    }

    @Test
    public void ignoraEspaciosSobrantes() {
        assertEquals("JP", iniciales("  Juan   Pérez  "));
    }

    @Test
    public void sinNombreDevuelveCadenaVacia() {
        assertEquals("", iniciales(""));
        assertEquals("", iniciales("   "));
        assertEquals("", iniciales(null));
    }
}
