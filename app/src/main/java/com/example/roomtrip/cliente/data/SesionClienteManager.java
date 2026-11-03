package com.example.roomtrip.cliente.data;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * Sesión del cliente en SharedPreferences ("cliente_sesion", MODE_PRIVATE).
 * Se guarda en data/data/com.example.roomtrip/shared_prefs/cliente_sesion.xml
 *
 * <p>Desde el Lab 6 quien autentica es Firebase Authentication. Aquí queda el uid y el nombre del
 * cliente (para el saludo sin esperar a Firestore) y los cobros que ya se le notificaron.
 */
public class SesionClienteManager {

    private static final String PREFS = "cliente_sesion";
    private static final String KEY_ROL = "rol";
    private static final String KEY_UID = "uid";
    private static final String KEY_NOMBRE = "nombre";
    private static final String KEY_COBROS_NOTIFICADOS = "cobrosNotificados";

    public static final String ROL_CLIENTE = "CLIENTE";

    private final SharedPreferences prefs;

    public SesionClienteManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    /** @param uid uid de Firebase Authentication (id del documento usuarios/{uid}) */
    public void iniciarSesion(String uid, String nombre) {
        prefs.edit()
                .putString(KEY_ROL, ROL_CLIENTE)
                .putString(KEY_UID, uid)
                .putString(KEY_NOMBRE, nombre)
                .apply();
    }

    public boolean haySesion() {
        return ROL_CLIENTE.equals(prefs.getString(KEY_ROL, null));
    }

    public String getUid() {
        return prefs.getString(KEY_UID, "");
    }

    /** Nombre completo del cliente (se copia en sus reservas). */
    public String getNombre() {
        return prefs.getString(KEY_NOMBRE, "");
    }

    public void setNombre(String nombre) {
        prefs.edit().putString(KEY_NOMBRE, nombre).apply();
    }

    /** Primer nombre para el saludo. */
    public String getPrimerNombre() {
        String nombre = getNombre().trim();
        int espacio = nombre.indexOf(' ');
        return espacio > 0 ? nombre.substring(0, espacio) : nombre;
    }

    /** RF-CL-05: el aviso de cobro se muestra una sola vez por reserva. */
    public boolean cobroYaNotificado(String reservaId) {
        return prefs.getStringSet(KEY_COBROS_NOTIFICADOS, new HashSet<>()).contains(reservaId);
    }

    public void marcarCobroNotificado(String reservaId) {
        // Se copia: el Set que devuelve getStringSet no se debe modificar
        Set<String> notificados = new HashSet<>(prefs.getStringSet(KEY_COBROS_NOTIFICADOS, new HashSet<>()));
        notificados.add(reservaId);
        prefs.edit().putStringSet(KEY_COBROS_NOTIFICADOS, notificados).apply();
    }

    public void cerrarSesion() {
        prefs.edit().remove(KEY_ROL).remove(KEY_UID).remove(KEY_NOMBRE).apply();
    }
}
