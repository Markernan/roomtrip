package com.example.roomtrip.data.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

/** RF-AH-02: un hotel necesita al menos 4 fotos para guardar su configuración. */
public class HotelFotosTest {

    @Test
    public void conMenosDeCuatroFotosNoSePuedeGuardar() {
        assertFalse(Hotel.tieneFotosSuficientes(null));
        assertFalse(Hotel.tieneFotosSuficientes(Collections.emptyList()));
        assertFalse(Hotel.tieneFotosSuficientes(Arrays.asList("a", "b", "c")));
    }

    @Test
    public void conCuatroOMasFotosSiSePuedeGuardar() {
        assertTrue(Hotel.tieneFotosSuficientes(Arrays.asList("a", "b", "c", "d")));
        assertTrue(Hotel.tieneFotosSuficientes(Arrays.asList("a", "b", "c", "d", "e")));
        assertEquals(4, Hotel.FOTOS_MINIMAS);
    }

    @Test
    public void laFotoPrincipalEsLaPrimera() {
        Hotel hotel = new Hotel();
        assertNull(hotel.fotoPrincipal());
        hotel.setFotos(Arrays.asList("https://x/1.jpg", "https://x/2.jpg"));
        assertEquals("https://x/1.jpg", hotel.fotoPrincipal());
        hotel.setFotos(null);
        assertNull(hotel.fotoPrincipal());
    }
}
