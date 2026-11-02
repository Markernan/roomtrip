package com.example.roomtrip.utils;

import android.util.Log;

import com.example.roomtrip.data.model.RolUsuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Registro de eventos relevantes en la colección {@code logs} de Firestore (requerimiento de calidad
 * "el sistema deberá registrar eventos relevantes"). Solo el Superadmin puede leerlos (ver
 * {@code firestore.rules}); su visor de auditoría los consultará desde aquí.
 *
 * <p>Si la escritura falla, se deja en el log de Android y el flujo del usuario sigue: un evento
 * que no se pudo registrar no debe impedir aceptar un pedido.
 */
public final class RegistroEventos {

    private static final String TAG = "RegistroEventos";

    public static final String COLECCION = "logs";

    // Tipos de evento del taxista
    public static final String TAXISTA_INICIO_SESION = "TAXISTA_INICIO_SESION";
    public static final String TAXISTA_CIERRE_SESION = "TAXISTA_CIERRE_SESION";
    public static final String TAXISTA_SOLICITUD_HABILITACION = "TAXISTA_SOLICITUD_HABILITACION";
    public static final String SERVICIO_TAXI_ACEPTADO = "SERVICIO_TAXI_ACEPTADO";
    public static final String SERVICIO_TAXI_CAMBIO_ESTADO = "SERVICIO_TAXI_CAMBIO_ESTADO";
    public static final String SERVICIO_TAXI_FINALIZADO = "SERVICIO_TAXI_FINALIZADO";

    // Otros roles (Lab 6: todos los roles usan Firebase)
    public static final String INICIO_SESION = "INICIO_SESION";
    public static final String CIERRE_SESION = "CIERRE_SESION";
    public static final String CLIENTE_REGISTRO = "CLIENTE_REGISTRO";
    public static final String ADMIN_HOTEL_ALTA = "ADMIN_HOTEL_ALTA";
    public static final String RESERVA_CREADA = "RESERVA_CREADA";
    public static final String CHECKOUT_SOLICITADO = "CHECKOUT_SOLICITADO";
    public static final String COBRO_REALIZADO = "COBRO_REALIZADO";
    public static final String SERVICIO_TAXI_SOLICITADO = "SERVICIO_TAXI_SOLICITADO";
    public static final String HOTEL_REGISTRADO = "HOTEL_REGISTRADO";
    public static final String HOTEL_ESTADO = "HOTEL_ESTADO";
    public static final String HOTEL_ACTUALIZADO = "HOTEL_ACTUALIZADO";
    public static final String USUARIO_ESTADO = "USUARIO_ESTADO";
    public static final String TAXISTA_APROBACION = "TAXISTA_APROBACION";
    public static final String DATOS_DEMO = "DATOS_DEMO";

    private RegistroEventos() {
    }

    /** Guarda {tipo, detalle, usuarioId, rol, fecha} con la hora del servidor. Requiere sesión iniciada. */
    public static void registrar(String tipo, RolUsuario rol, String detalle) {
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        if (usuario == null) {
            Log.w(TAG, "Evento " + tipo + " sin sesión iniciada: no se registra");
            return;
        }
        Map<String, Object> evento = new HashMap<>();
        evento.put("tipo", tipo);
        evento.put("detalle", detalle);
        evento.put("usuarioId", usuario.getUid());
        evento.put("rol", rol.name());
        evento.put("fecha", FieldValue.serverTimestamp());

        FirebaseFirestore.getInstance().collection(COLECCION).add(evento)
                .addOnFailureListener(e -> Log.w(TAG, "No se pudo registrar el evento " + tipo, e));
    }
}
