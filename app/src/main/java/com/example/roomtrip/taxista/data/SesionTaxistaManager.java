package com.example.roomtrip.taxista.data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Sesión del taxista en SharedPreferences ("sesion", MODE_PRIVATE).
 * Se guarda en data/data/com.example.roomtrip/shared_prefs/sesion.xml
 */
public class SesionTaxistaManager {

    private static final String PREFS = "sesion";

    public static final String ROL_TAXISTA = "TAXISTA";

    // Id simulado hasta tener backend (Firebase en el siguiente lab)
    public static final String ID_TAXISTA_DEMO = "TX-001";

    private static final String KEY_ROL = "rol";
    private static final String KEY_ID = "idTaxista";
    private static final String KEY_NOMBRE = "nombre";
    private static final String KEY_DISPONIBLE = "disponible";

    private final SharedPreferences prefs;

    public SesionTaxistaManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void iniciarSesion(String idTaxista, String nombre) {
        prefs.edit()
                .putString(KEY_ROL, ROL_TAXISTA)
                .putString(KEY_ID, idTaxista)
                .putString(KEY_NOMBRE, nombre)
                .putBoolean(KEY_DISPONIBLE, true)
                .apply();
    }

    public boolean haySesionTaxista() {
        return ROL_TAXISTA.equals(prefs.getString(KEY_ROL, null));
    }

    public String getIdTaxista() {
        return prefs.getString(KEY_ID, "");
    }

    public String getNombre() {
        return prefs.getString(KEY_NOMBRE, "");
    }

    public boolean isDisponible() {
        return prefs.getBoolean(KEY_DISPONIBLE, true);
    }

    public void setDisponible(boolean disponible) {
        prefs.edit().putBoolean(KEY_DISPONIBLE, disponible).apply();
    }

    /** Borra solo las claves del taxista para no afectar a otros roles que usen el mismo archivo. */
    public void cerrarSesion() {
        prefs.edit()
                .remove(KEY_ROL)
                .remove(KEY_ID)
                .remove(KEY_NOMBRE)
                .remove(KEY_DISPONIBLE)
                .apply();
    }
}
