package com.example.roomtrip.data.firebase;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.roomtrip.data.model.EstadoServicioTaxi;
import com.example.roomtrip.data.model.ServicioTaxi;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Acceso a la colección {@code serviciosTaxi} de Cloud Firestore desde el rol taxista (Lab 6).
 * Reemplaza a los JSON locales del Lab 5: ahora el servicio lo ven también el cliente y el hotel.
 *
 * <ul>
 *   <li>Lecturas en tiempo real con {@code addSnapshotListener} (pedidos y servicio en curso): quien
 *       llama guarda el {@link ListenerRegistration} y lo quita en {@code onStop} (Clase 9.2).</li>
 *   <li>Cambios de estado con transacciones: si dos taxistas aceptan el mismo pedido a la vez, solo
 *       uno lo consigue (arquitectura.md, regla 2). Las reglas de seguridad repiten la validación
 *       en el servidor.</li>
 *   <li>Las consultas usan un solo filtro de igualdad y se ordenan en el teléfono, así no hace falta
 *       crear índices compuestos en la consola.</li>
 * </ul>
 */
public class ServicioTaxiRepository {

    private static final String TAG = "ServicioTaxiRepo";

    /** Respuesta de {@link #escucharPedidosDisponibles}. */
    public interface ListenerPedidos {
        /**
         * @param pedidos pedidos disponibles, el más antiguo primero
         * @param nuevos  los que aparecieron en este cambio (en la primera respuesta son todos)
         */
        void onPedidos(@NonNull List<ServicioTaxi> pedidos, @NonNull List<ServicioTaxi> nuevos);

        void onFallo(@NonNull Exception e);
    }

    private final FirebaseFirestore db;

    public ServicioTaxiRepository() {
        db = FirebaseFirestore.getInstance();
    }

    private CollectionReference servicios() {
        return db.collection(ServicioTaxi.COLECCION);
    }

    // ---------- Pedidos disponibles (SOLICITADO) ----------

    /** Escucha en tiempo real los pedidos que nadie ha aceptado todavía (RF-TAX-005). */
    public ListenerRegistration escucharPedidosDisponibles(@NonNull ListenerPedidos listener) {
        return servicios()
                .whereEqualTo(ServicioTaxi.CAMPO_ESTADO, EstadoServicioTaxi.SOLICITADO.name())
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null) {
                        listener.onFallo(error);
                        return;
                    }
                    if (snapshot == null) return;

                    List<ServicioTaxi> pedidos = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        ServicioTaxi s = convertir(doc);
                        if (s != null && s.puedeSerAceptado()) pedidos.add(s);
                    }
                    // El más antiguo primero; uno recién creado (fecha del servidor aún pendiente) al final
                    Collections.sort(pedidos, Comparator.comparing(ServicioTaxi::getFechaSolicitud,
                            Comparator.nullsLast(Comparator.<Date>naturalOrder())));

                    List<ServicioTaxi> nuevos = new ArrayList<>();
                    for (DocumentChange cambio : snapshot.getDocumentChanges()) {
                        if (cambio.getType() != DocumentChange.Type.ADDED) continue;
                        ServicioTaxi s = convertir(cambio.getDocument());
                        if (s != null && s.puedeSerAceptado()) nuevos.add(s);
                    }
                    listener.onPedidos(pedidos, nuevos);
                });
    }

    /**
     * Crea un pedido con id autogenerado ({@code add()}, Clase 9.2). Lo crea el cliente al pedir el
     * taxi en el checkout; el botón "Simular nuevo pedido" del taxista también lo usa. Devuelve el id.
     */
    public void crearPedido(@NonNull ServicioTaxi pedido, @NonNull Callbacks.Exito<String> exito,
                            @NonNull Callbacks.Fallo fallo) {
        servicios().add(pedido)
                .addOnSuccessListener(ref -> exito.onExito(ref.getId()))
                .addOnFailureListener(fallo::onFallo);
    }

    /**
     * Acepta un pedido: SOLICITADO → ASIGNADO y queda a nombre del taxista. Es una transacción:
     * lee el pedido y solo escribe si sigue libre. Devuelve {@code false} si otro taxista lo tomó
     * primero (en ese caso no se escribe nada).
     */
    public void aceptarPedido(@NonNull String servicioId, @NonNull String uidTaxista, @NonNull String nombreTaxista,
                              @NonNull Callbacks.Exito<Boolean> exito, @NonNull Callbacks.Fallo fallo) {
        DocumentReference ref = servicios().document(servicioId);
        db.runTransaction(tx -> {
            ServicioTaxi actual = convertir(tx.get(ref));
            if (actual == null || !actual.puedeSerAceptado()) {
                return false;
            }
            Map<String, Object> cambios = new HashMap<>();
            cambios.put(ServicioTaxi.CAMPO_ESTADO, EstadoServicioTaxi.ASIGNADO.name());
            cambios.put(ServicioTaxi.CAMPO_TAXISTA_ID, uidTaxista);
            cambios.put(ServicioTaxi.CAMPO_TAXISTA_NOMBRE, nombreTaxista);
            cambios.put(ServicioTaxi.CAMPO_FECHA_ASIGNACION, FieldValue.serverTimestamp());
            cambios.put(ServicioTaxi.CAMPO_FECHA_ACTUALIZACION, FieldValue.serverTimestamp());
            tx.update(ref, cambios);
            return true;
        }).addOnSuccessListener(exito::onExito).addOnFailureListener(fallo::onFallo);
    }

    // ---------- Un servicio concreto (cliente) y los del hotel (admin) ----------

    /** Escucha un servicio por id: el cliente ve cuándo lo aceptan y cómo avanza. Null si no existe. */
    public ListenerRegistration escucharServicio(@NonNull String servicioId,
                                                 @NonNull Callbacks.Exito<ServicioTaxi> cambio,
                                                 @NonNull Callbacks.Fallo fallo) {
        return servicios().document(servicioId).addSnapshotListener((doc, error) -> {
            if (error != null) {
                fallo.onFallo(error);
                return;
            }
            cambio.onExito(doc != null ? convertir(doc) : null);
        });
    }

    /** Traslados del hotel en tiempo real (admin de hotel), el más reciente primero. */
    public ListenerRegistration escucharDeHotel(@NonNull String hotelId,
                                                @NonNull Callbacks.Exito<List<ServicioTaxi>> cambio,
                                                @NonNull Callbacks.Fallo fallo) {
        return servicios().whereEqualTo(ServicioTaxi.CAMPO_HOTEL_ID, hotelId)
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null) {
                        fallo.onFallo(error);
                        return;
                    }
                    List<ServicioTaxi> lista = new ArrayList<>();
                    if (snapshot != null) {
                        for (DocumentSnapshot doc : snapshot.getDocuments()) {
                            ServicioTaxi s = convertir(doc);
                            if (s != null) lista.add(s);
                        }
                    }
                    Collections.sort(lista, Comparator.comparing(ServicioTaxi::getFechaSolicitud,
                            Comparator.nullsFirst(Comparator.<Date>naturalOrder())).reversed());
                    cambio.onExito(lista);
                });
    }

    // ---------- Servicio en curso del taxista ----------

    /** El servicio ASIGNADO / EN_CAMINO / EN_TRASLADO del taxista, leído una vez ({@code get()}). Null si no hay. */
    public void obtenerServicioEnCurso(@NonNull String uidTaxista, @NonNull Callbacks.Exito<ServicioTaxi> exito,
                                       @NonNull Callbacks.Fallo fallo) {
        servicios().whereEqualTo(ServicioTaxi.CAMPO_TAXISTA_ID, uidTaxista).get()
                .addOnSuccessListener(snapshot -> exito.onExito(enCurso(snapshot)))
                .addOnFailureListener(fallo::onFallo);
    }

    /** Igual que {@link #obtenerServicioEnCurso}, pero avisa cada vez que cambia. Null si ya no hay. */
    public ListenerRegistration escucharServicioEnCurso(@NonNull String uidTaxista,
                                                        @NonNull Callbacks.Exito<ServicioTaxi> cambio,
                                                        @NonNull Callbacks.Fallo fallo) {
        return servicios().whereEqualTo(ServicioTaxi.CAMPO_TAXISTA_ID, uidTaxista)
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null) {
                        fallo.onFallo(error);
                        return;
                    }
                    cambio.onExito(enCurso(snapshot));
                });
    }

    /**
     * RN-010: ASIGNADO → EN_CAMINO → EN_TRASLADO, sin saltarse estados. Devuelve el nuevo estado,
     * o null si el servicio ya no estaba en un estado que se pueda avanzar (por ejemplo, porque se
     * avanzó desde otro dispositivo).
     */
    public void avanzarEstado(@NonNull String servicioId, @NonNull String uidTaxista,
                              @NonNull Callbacks.Exito<EstadoServicioTaxi> exito, @NonNull Callbacks.Fallo fallo) {
        DocumentReference ref = servicios().document(servicioId);
        db.runTransaction(tx -> {
            ServicioTaxi actual = convertir(tx.get(ref));
            if (actual == null || !actual.esDelTaxista(uidTaxista)) {
                return null;
            }
            EstadoServicioTaxi siguiente = actual.siguienteEstadoManual();
            if (siguiente == null) {
                return null;
            }
            tx.update(ref,
                    ServicioTaxi.CAMPO_ESTADO, siguiente.name(),
                    ServicioTaxi.CAMPO_FECHA_ACTUALIZACION, FieldValue.serverTimestamp());
            return siguiente;
        }).addOnSuccessListener(exito::onExito).addOnFailureListener(fallo::onFallo);
    }

    /**
     * RN-011: registra FINALIZADO solo si el código leído es el id de este servicio y el pasajero
     * va en el taxi (EN_TRASLADO). Devuelve el servicio finalizado, o null si el QR no corresponde.
     */
    public void finalizarConQr(@NonNull String servicioId, @NonNull String codigoLeido, @NonNull String uidTaxista,
                               @NonNull Callbacks.Exito<ServicioTaxi> exito, @NonNull Callbacks.Fallo fallo) {
        DocumentReference ref = servicios().document(servicioId);
        db.runTransaction(tx -> {
            ServicioTaxi actual = convertir(tx.get(ref));
            if (actual == null || !actual.esDelTaxista(uidTaxista) || !actual.qrValido(codigoLeido)) {
                return null;
            }
            tx.update(ref,
                    ServicioTaxi.CAMPO_ESTADO, EstadoServicioTaxi.FINALIZADO.name(),
                    ServicioTaxi.CAMPO_FECHA_FINALIZACION, FieldValue.serverTimestamp(),
                    ServicioTaxi.CAMPO_FECHA_ACTUALIZACION, FieldValue.serverTimestamp());
            // Copia local para la notificación; en Firestore queda la hora del servidor
            actual.setEstado(EstadoServicioTaxi.FINALIZADO);
            actual.setFechaFinalizacion(new Date());
            return actual;
        }).addOnSuccessListener(exito::onExito).addOnFailureListener(fallo::onFallo);
    }

    // ---------- Historial ----------

    /** Servicios FINALIZADOS del taxista, el más reciente primero (una sola lectura). */
    public void obtenerHistorial(@NonNull String uidTaxista, @NonNull Callbacks.Exito<List<ServicioTaxi>> exito,
                                 @NonNull Callbacks.Fallo fallo) {
        servicios().whereEqualTo(ServicioTaxi.CAMPO_TAXISTA_ID, uidTaxista).get()
                .addOnSuccessListener(snapshot -> {
                    List<ServicioTaxi> historial = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        ServicioTaxi s = convertir(doc);
                        if (s != null && s.getEstado() == EstadoServicioTaxi.FINALIZADO) historial.add(s);
                    }
                    Collections.sort(historial, Comparator.comparing(ServicioTaxi::getFechaFinalizacion,
                            Comparator.nullsFirst(Comparator.<Date>naturalOrder())).reversed());
                    exito.onExito(historial);
                })
                .addOnFailureListener(fallo::onFallo);
    }

    // ---------- Helpers ----------

    @Nullable
    private static ServicioTaxi enCurso(@Nullable QuerySnapshot snapshot) {
        if (snapshot == null) return null;
        for (DocumentSnapshot doc : snapshot.getDocuments()) {
            ServicioTaxi s = convertir(doc);
            if (s != null && s.estaEnCurso()) return s;
        }
        return null;
    }

    /**
     * {@code toObject()} lanza una excepción si un documento tiene un valor que no encaja (por ejemplo,
     * un estado escrito a mano en la consola como "En camino"). Ese documento se ignora y queda en el log.
     */
    @Nullable
    private static ServicioTaxi convertir(@NonNull DocumentSnapshot doc) {
        if (!doc.exists()) return null;
        try {
            return doc.toObject(ServicioTaxi.class);
        } catch (RuntimeException e) {
            Log.e(TAG, "serviciosTaxi/" + doc.getId() + " tiene un formato inválido y se ignora", e);
            return null;
        }
    }
}
