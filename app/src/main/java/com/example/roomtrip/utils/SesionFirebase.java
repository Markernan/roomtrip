package com.example.roomtrip.utils;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.roomtrip.adminhotel.AdminSession;
import com.example.roomtrip.cliente.data.SesionClienteManager;
import com.example.roomtrip.cliente.notificaciones.ClienteNotificationHelper;
import com.example.roomtrip.data.model.RolUsuario;
import com.example.roomtrip.login.LoginActivity;
import com.example.roomtrip.taxista.data.SesionTaxistaManager;
import com.example.roomtrip.taxista.notificaciones.TaxiNotificationHelper;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Sesión de Firebase Authentication compartida por los cuatro roles: obtener el uid en cada pantalla
 * y cerrar sesión (Clase 10: {@code AuthUI.signOut}).
 */
public final class SesionFirebase {

    private static final String TAG = "SesionFirebase";

    private SesionFirebase() {
    }

    /**
     * Devuelve el uid del usuario autenticado. Si no hay sesión (se cerró o expiró), abre el login,
     * cierra la pantalla actual y devuelve null: quien llama debe hacer {@code return}.
     */
    @Nullable
    public static String uidOVolverAlLogin(Activity activity) {
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        if (usuario != null) {
            return usuario.getUid();
        }
        irAlLogin(activity);
        return null;
    }

    /** Uid actual o null, sin navegar (para fragments y helpers). */
    @Nullable
    public static String uidActual() {
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        return usuario != null ? usuario.getUid() : null;
    }

    /**
     * Cierra la sesión de cualquier rol: registra el evento (antes, porque después ya no hay usuario
     * para escribir en logs), cierra Firebase y Google con AuthUI, limpia las sesiones locales y sus
     * notificaciones, y vuelve al login sin dejar pantallas atrás.
     */
    public static void cerrarSesion(Activity activity, RolUsuario rol) {
        RegistroEventos.registrar(RegistroEventos.CIERRE_SESION, rol, "Cierre de sesión");
        AuthUI.getInstance().signOut(activity).addOnCompleteListener(tarea -> {
            if (!tarea.isSuccessful()) {
                Log.w(TAG, "signOut terminó con error; se limpia la sesión local igual", tarea.getException());
            }
            limpiarSesionesLocales(activity);
            irAlLogin(activity);
        });
    }

    /** Borra lo que cada rol guarda en SharedPreferences y sus notificaciones pendientes. */
    public static void limpiarSesionesLocales(Activity activity) {
        new SesionTaxistaManager(activity).cerrarSesion();
        new SesionClienteManager(activity).cerrarSesion();
        AdminSession.cerrarSesion(activity);
        new TaxiNotificationHelper(activity).cancelarTodas();
        new ClienteNotificationHelper(activity).cancelarTodas();
    }

    private static void irAlLogin(Activity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        activity.finish();
    }
}
