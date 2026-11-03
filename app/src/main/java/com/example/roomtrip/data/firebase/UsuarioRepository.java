package com.example.roomtrip.data.firebase;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.roomtrip.data.model.EstadoAprobacion;
import com.example.roomtrip.data.model.EstadoCuenta;
import com.example.roomtrip.data.model.PerfilUsuario;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Usuarios de la app en {@code usuarios/{uid}} (el id es el uid de Firebase Authentication) y las
 * invitaciones de administradores de hotel en {@code invitaciones/{correo}}.
 */
public class UsuarioRepository {

    private static final String TAG = "UsuarioRepository";

    public static final String COLECCION_INVITACIONES = "invitaciones";

    /** Invitación que deja el Superadmin al registrar un hotel: el correo será su administrador. */
    public static class Invitacion {
        public final String hotelId;
        public final String hotelNombre;

        Invitacion(String hotelId, String hotelNombre) {
            this.hotelId = hotelId;
            this.hotelNombre = hotelNombre;
        }
    }

    private final FirebaseFirestore db;

    public UsuarioRepository() {
        db = FirebaseFirestore.getInstance();
    }

    private DocumentReference documento(String uid) {
        return db.collection(PerfilUsuario.COLECCION).document(uid);
    }

    /** Las invitaciones se guardan con el correo en minúsculas como id. */
    public static String claveCorreo(String correo) {
        return correo == null ? "" : correo.trim().toLowerCase(Locale.ROOT);
    }

    // ---------- Perfil ----------

    /** Lee el perfil una vez ({@code get()}). Devuelve null si el usuario todavía no tiene documento. */
    public void obtener(@NonNull String uid, @NonNull Callbacks.Exito<PerfilUsuario> exito,
                        @NonNull Callbacks.Fallo fallo) {
        documento(uid).get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        exito.onExito(null);
                        return;
                    }
                    PerfilUsuario perfil = convertir(doc);
                    if (perfil == null) {
                        fallo.onFallo(new IllegalStateException("usuarios/" + uid + " tiene un formato inválido"));
                        return;
                    }
                    exito.onExito(perfil);
                })
                .addOnFailureListener(fallo::onFallo);
    }

    /** Crea el documento del usuario con {@code set()}. */
    public void crear(@NonNull String uid, @NonNull PerfilUsuario perfil,
                      @NonNull Callbacks.Exito<Void> exito, @NonNull Callbacks.Fallo fallo) {
        documento(uid).set(perfil)
                .addOnSuccessListener(unused -> exito.onExito(null))
                .addOnFailureListener(fallo::onFallo);
    }

    /** Actualiza solo los campos indicados ({@code update()}). Las reglas deciden quién puede qué. */
    public void actualizar(@NonNull String uid, @NonNull Map<String, Object> cambios,
                           @NonNull Callbacks.Exito<Void> exito, @NonNull Callbacks.Fallo fallo) {
        documento(uid).update(cambios)
                .addOnSuccessListener(unused -> exito.onExito(null))
                .addOnFailureListener(fallo::onFallo);
    }

    // ---------- Superadmin ----------

    /** Todos los usuarios, ordenados por nombre (lo puede leer solo el Superadmin). */
    public void obtenerTodos(@NonNull Callbacks.Exito<List<PerfilUsuario>> exito, @NonNull Callbacks.Fallo fallo) {
        db.collection(PerfilUsuario.COLECCION).get()
                .addOnSuccessListener(snapshot -> {
                    List<PerfilUsuario> lista = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        PerfilUsuario p = convertir(doc);
                        if (p != null) lista.add(p);
                    }
                    lista.sort((a, b) -> a.nombreCompleto().compareToIgnoreCase(b.nombreCompleto()));
                    exito.onExito(lista);
                })
                .addOnFailureListener(fallo::onFallo);
    }

    /** Usuarios que administran un hotel (para el detalle del hotel del Superadmin). */
    public void obtenerAdminsDeHotel(@NonNull String hotelId, @NonNull Callbacks.Exito<List<PerfilUsuario>> exito,
                                     @NonNull Callbacks.Fallo fallo) {
        db.collection(PerfilUsuario.COLECCION).whereEqualTo(PerfilUsuario.CAMPO_HOTEL_ID, hotelId).get()
                .addOnSuccessListener(snapshot -> {
                    List<PerfilUsuario> lista = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        PerfilUsuario p = convertir(doc);
                        if (p != null) lista.add(p);
                    }
                    exito.onExito(lista);
                })
                .addOnFailureListener(fallo::onFallo);
    }

    /** RF-USR-007: activar o desactivar una cuenta (Superadmin). */
    public void cambiarEstado(@NonNull String uid, @NonNull EstadoCuenta estado,
                              @NonNull Callbacks.Exito<Void> exito, @NonNull Callbacks.Fallo fallo) {
        Map<String, Object> cambios = new HashMap<>();
        cambios.put(PerfilUsuario.CAMPO_ESTADO, estado.name());
        actualizar(uid, cambios, exito, fallo);
    }

    /** RF-WTX-004: aprobar o rechazar a un taxista (Superadmin). */
    public void cambiarAprobacion(@NonNull String uid, @NonNull EstadoAprobacion aprobacion,
                                  @NonNull Callbacks.Exito<Void> exito, @NonNull Callbacks.Fallo fallo) {
        Map<String, Object> cambios = new HashMap<>();
        cambios.put(PerfilUsuario.CAMPO_ESTADO_APROBACION, aprobacion.name());
        actualizar(uid, cambios, exito, fallo);
    }

    // ---------- Invitaciones de administradores de hotel ----------

    /** Invitación pendiente para ese correo, o null si no hay. */
    public void obtenerInvitacion(@Nullable String correo, @NonNull Callbacks.Exito<Invitacion> exito,
                                  @NonNull Callbacks.Fallo fallo) {
        String clave = claveCorreo(correo);
        if (clave.isEmpty()) {
            exito.onExito(null);
            return;
        }
        db.collection(COLECCION_INVITACIONES).document(clave).get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        exito.onExito(null);
                        return;
                    }
                    exito.onExito(new Invitacion(doc.getString("hotelId"), doc.getString("hotelNombre")));
                })
                .addOnFailureListener(fallo::onFallo);
    }

    /** Datos de una invitación nueva (la escribe HotelRepository junto con el hotel, en un batch). */
    static Map<String, Object> datosInvitacion(String hotelId, String hotelNombre) {
        Map<String, Object> datos = new HashMap<>();
        datos.put("hotelId", hotelId);
        datos.put("hotelNombre", hotelNombre);
        datos.put("rol", "ADMIN_HOTEL");
        datos.put("fecha", FieldValue.serverTimestamp());
        return datos;
    }

    // ---------- Helpers ----------

    /** {@code toObject()} lanza una excepción si un valor no encaja (p. ej. rol "Taxista" escrito a mano). */
    @Nullable
    static PerfilUsuario convertir(@NonNull DocumentSnapshot doc) {
        try {
            return doc.toObject(PerfilUsuario.class);
        } catch (RuntimeException e) {
            Log.e(TAG, "usuarios/" + doc.getId() + " tiene un formato inválido y se ignora", e);
            return null;
        }
    }
}
