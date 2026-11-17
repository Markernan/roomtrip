package com.example.roomtrip.data.firebase;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/** Saca la ruta de un archivo de Firebase Storage a partir de su URL de descarga (lógica pura, sin Android). */
public final class RutaStorage {

    private RutaStorage() {
    }

    /**
     * {@code .../v0/b/<bucket>/o/hoteles%2F123%2Ffoto.jpg?alt=media&token=...} -> {@code hoteles/123/foto.jpg}, o null
     * si la URL no tiene esa forma. Sirve también con el emulador, donde el host es otro.
     */
    public static String desdeUrl(String url) {
        if (url == null) return null;
        int inicio = url.indexOf("/o/");
        if (inicio < 0) return null;
        String resto = url.substring(inicio + 3);
        int fin = resto.indexOf('?');
        if (fin >= 0) resto = resto.substring(0, fin);
        if (resto.isEmpty()) return null;
        try {
            return URLDecoder.decode(resto, "UTF-8");
        } catch (UnsupportedEncodingException | IllegalArgumentException e) {
            return null;
        }
    }
}
