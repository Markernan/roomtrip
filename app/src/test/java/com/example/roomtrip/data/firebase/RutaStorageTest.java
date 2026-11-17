package com.example.roomtrip.data.firebase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/** La ruta del archivo se saca de la URL de descarga para poder borrarlo, también con el emulador. */
public class RutaStorageTest {

    @Test
    public void sacaLaRutaDeUnaUrlDeProduccion() {
        String url = "https://firebasestorage.googleapis.com/v0/b/roomtrip-95ae6.firebasestorage.app/o/"
                + "hoteles%2Fh1%2F170_ab.jpg?alt=media&token=abc";
        assertEquals("hoteles/h1/170_ab.jpg", RutaStorage.desdeUrl(url));
    }

    @Test
    public void sacaLaRutaDeUnaUrlDelEmulador() {
        String url = "http://10.0.2.2:9199/v0/b/roomtrip-95ae6.firebasestorage.app/o/perfiles%2Fuid1.jpg?alt=media";
        assertEquals("perfiles/uid1.jpg", RutaStorage.desdeUrl(url));
    }

    @Test
    public void unaUrlQueNoEsDeStorageDevuelveNull() {
        assertNull(RutaStorage.desdeUrl(null));
        assertNull(RutaStorage.desdeUrl(""));
        assertNull(RutaStorage.desdeUrl("https://example.com/foto.jpg"));
        assertNull(RutaStorage.desdeUrl("https://x/o/"));
    }
}
