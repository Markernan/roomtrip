package com.example.roomtrip.cliente.data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Sesión del cliente en SharedPreferences ("cliente_sesion", MODE_PRIVATE).
 * Se guarda en data/data/com.example.roomtrip/shared_prefs/cliente_sesion.xml
 */
public class SesionClienteManager {

    private static final String PREFS = "cliente_sesion";
    private static final String KEY_ROL = "rol";

    public static final String ROL_CLIENTE = "CLIENTE";

    private final SharedPreferences prefs;

    public SesionClienteManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void iniciarSesion() {
        prefs.edit().putString(KEY_ROL, ROL_CLIENTE).apply();
    }

    public boolean haySesion() {
        return ROL_CLIENTE.equals(prefs.getString(KEY_ROL, null));
    }

    /** Cierra la sesión pero conserva la cuenta registrada (PerfilClienteRepository). */
    public void cerrarSesion() {
        prefs.edit().remove(KEY_ROL).apply();
    }
}
