package com.example.roomtrip.adminhotel;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Gestión de la sesión y preferencias del Administrador de Hotel.
 */
public final class AdminSession {
    private static final String PREFS = "admin_prefs";
    
    private AdminSession() {}

    private static SharedPreferences p(Context c) {
        return c.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public static int incrementarAperturas(Context c) {
        int n = p(c).getInt("aperturas", 0) + 1;
        p(c).edit().putInt("aperturas", n).apply();
        return n;
    }

    /** Lab 6: el hotel del administrador viene de usuarios/{uid}.hotelId (id del documento en Firestore). */
    public static void guardarHotel(Context c, String id, String nombre) {
        p(c).edit()
            .putString("hotel_id_firestore", id)
            .putString("hotel_nombre", nombre)
            .apply();
    }

    /** Id del documento hoteles/{id}; cadena vacía si no hay sesión de administrador. */
    public static String getHotelId(Context c) {
        return p(c).getString("hotel_id_firestore", "");
    }

    /** Al cerrar sesión: el siguiente administrador puede ser de otro hotel. */
    public static void cerrarSesion(Context c) {
        p(c).edit().remove("hotel_id_firestore").remove("hotel_nombre").apply();
    }

    public static String getHotelNombre(Context c) {
        return p(c).getString("hotel_nombre", "");
    }

    public static void guardarFiltroReservas(Context c, String estado, String fechaInicio, String fechaFin) {
        p(c).edit()
            .putString("filtro_res_estado", estado)
            .putString("filtro_res_inicio", fechaInicio)
            .putString("filtro_res_fin", fechaFin)
            .apply();
    }

    public static String getFiltroReservaEstado(Context c) {
        return p(c).getString("filtro_res_estado", "TODOS");
    }

    public static void guardarFiltroHabitaciones(Context c, String busqueda) {
        p(c).edit().putString("filtro_hab_busqueda", busqueda).apply();
    }

    public static String getFiltroHabitaciones(Context c) {
        return p(c).getString("filtro_hab_busqueda", "");
    }

    public static void guardarUltimaPestania(Context c, String nombre) {
        p(c).edit().putString("ultima_pestania", nombre).apply();
    }

    public static String getUltimaPestania(Context c) {
        return p(c).getString("ultima_pestania", "inicio");
    }
    
    public static void setPermisoPedido(Context c, boolean pedido) {
        p(c).edit().putBoolean("permiso_pedido", pedido).apply();
    }
    
    public static boolean isPermisoPedido(Context c) {
        return p(c).getBoolean("permiso_pedido", false);
    }
}
