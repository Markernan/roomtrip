package com.example.roomtrip.cliente;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class UbicacionHotelTest {

    @Test
    public void zonaDe_quitaLaCalle() {
        assertEquals("Miraflores, Lima", UbicacionHotel.zonaDe("Av. Larco 245, Miraflores, Lima"));
    }

    @Test
    public void zonaDe_sinComasDevuelveElTexto() {
        assertEquals("Cusco", UbicacionHotel.zonaDe("Cusco"));
    }

    @Test
    public void zonaDe_vacioONulo() {
        assertNull(UbicacionHotel.zonaDe(null));
        assertNull(UbicacionHotel.zonaDe("  "));
        assertNull(UbicacionHotel.zonaDe("Av. Larco 245,"));
    }
}
