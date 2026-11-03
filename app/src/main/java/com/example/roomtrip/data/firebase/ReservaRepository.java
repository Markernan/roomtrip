package com.example.roomtrip.data.firebase;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.roomtrip.data.model.EstadoCheckout;
import com.example.roomtrip.data.model.EstadoReserva;
import com.example.roomtrip.data.model.Hotel;
import com.example.roomtrip.data.model.Reserva;
import com.example.roomtrip.data.model.ValoracionHotel;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reservas en {@code reservas/{id}}.
 *
 * <p><b>No superposición (RN-001 y RN-002).</b> Las transacciones del SDK de Android no pueden
 * consultar colecciones, solo leer documentos por id. Por eso cada noche reservada deja un documento
 * "candado" en {@code ocupaciones}: {@code hab_{habitacionId}_{fecha}} (la habitación) y
 * {@code cli_{clienteId}_{fecha}} (el cliente). La transacción lee los candados de todas las noches;
 * si alguno existe, la reserva se rechaza sin escribir nada. Si dos clientes reservan a la vez, solo
 * uno gana: Firestore reintenta la transacción del otro, que entonces ve el candado.
 */
public class ReservaRepository {

    private static final String TAG = "ReservaRepository";

    public static final String COLECCION_OCUPACIONES = "ocupaciones";

    /** Resultado de intentar reservar. */
    public enum Resultado {
        CONFIRMADA,
        /** RN-001: otra reserva ya tiene esa habitación en alguna de las noches. */
        HABITACION_OCUPADA,
        /** RN-002: el cliente ya tiene otra reserva que se cruza con estas fechas. */
        CLIENTE_CON_RESERVA,
        /** Rango vacío o más largo que Reserva.MAXIMO_NOCHES. */
        FECHAS_INVALIDAS
    }

    private final FirebaseFirestore db;

    public ReservaRepository() {
        db = FirebaseFirestore.getInstance();
    }

    private CollectionReference reservas() {
        return db.collection(Reserva.COLECCION);
    }

    private DocumentReference candadoHabitacion(String habitacionId, String noche) {
        return db.collection(COLECCION_OCUPACIONES).document("hab_" + habitacionId + "_" + noche);
    }

    private DocumentReference candadoCliente(String clienteId, String noche) {
        return db.collection(COLECCION_OCUPACIONES).document("cli_" + clienteId + "_" + noche);
    }

    // ---------- Reservar (cliente) ----------

    /**
     * Revisión previa para avisar en pantalla (tvAlertaConflicto) antes de pagar. No reemplaza a la
     * transacción de {@link #reservar}: entre esta lectura y el pago otro cliente podría reservar.
     */
    public void verificarDisponibilidad(@NonNull String habitacionId, @NonNull String clienteId,
                                        @NonNull String entrada, @NonNull String salida,
                                        @NonNull Callbacks.Exito<Resultado> exito, @NonNull Callbacks.Fallo fallo) {
        List<String> noches = Reserva.nochesEntre(entrada, salida);
        if (noches.isEmpty() || noches.size() > Reserva.MAXIMO_NOCHES) {
            exito.onExito(Resultado.FECHAS_INVALIDAS);
            return;
        }
        List<Task<DocumentSnapshot>> lecturas = new ArrayList<>();
        for (String noche : noches) {
            lecturas.add(candadoHabitacion(habitacionId, noche).get());
            lecturas.add(candadoCliente(clienteId, noche).get());
        }
        Tasks.whenAllSuccess(lecturas)
                .addOnSuccessListener(resultados -> {
                    for (int i = 0; i < resultados.size(); i++) {
                        if (((DocumentSnapshot) resultados.get(i)).exists()) {
                            exito.onExito(i % 2 == 0 ? Resultado.HABITACION_OCUPADA : Resultado.CLIENTE_CON_RESERVA);
                            return;
                        }
                    }
                    exito.onExito(Resultado.CONFIRMADA);
                })
                .addOnFailureListener(fallo::onFallo);
    }

    /**
     * Crea la reserva CONFIRMADA si ninguna noche está tomada (transacción). Al terminar,
     * {@code reserva.getId()} tiene el id del documento creado.
     */
    public void reservar(@NonNull Reserva reserva, @NonNull Callbacks.Exito<Resultado> exito,
                         @NonNull Callbacks.Fallo fallo) {
        List<String> noches = Reserva.nochesEntre(reserva.getFechaEntrada(), reserva.getFechaSalida());
        if (noches.isEmpty() || noches.size() > Reserva.MAXIMO_NOCHES) {
            exito.onExito(Resultado.FECHAS_INVALIDAS);
            return;
        }
        DocumentReference ref = reservas().document();
        reserva.setId(ref.getId());   // @DocumentId: no se escribe, pero quien llama conoce el id
        reserva.setEstado(EstadoReserva.CONFIRMADA);

        db.runTransaction(tx -> {
            // En una transacción todas las lecturas van antes que las escrituras
            for (String noche : noches) {
                if (tx.get(candadoHabitacion(reserva.getHabitacionId(), noche)).exists()) {
                    return Resultado.HABITACION_OCUPADA;
                }
                if (tx.get(candadoCliente(reserva.getClienteId(), noche)).exists()) {
                    return Resultado.CLIENTE_CON_RESERVA;
                }
            }
            for (String noche : noches) {
                Map<String, Object> candado = new HashMap<>();
                candado.put("reservaId", ref.getId());
                candado.put("clienteId", reserva.getClienteId());
                candado.put("hotelId", reserva.getHotelId());
                candado.put("habitacionId", reserva.getHabitacionId());
                candado.put("noche", noche);
                tx.set(candadoHabitacion(reserva.getHabitacionId(), noche), candado);
                tx.set(candadoCliente(reserva.getClienteId(), noche), candado);
            }
            tx.set(ref, reserva);
            return Resultado.CONFIRMADA;
        }).addOnSuccessListener(exito::onExito).addOnFailureListener(fallo::onFallo);
    }

    // ---------- Lecturas ----------

    public void obtener(@NonNull String reservaId, @NonNull Callbacks.Exito<Reserva> exito,
                        @NonNull Callbacks.Fallo fallo) {
        reservas().document(reservaId).get()
                .addOnSuccessListener(doc -> exito.onExito(doc.exists() ? convertir(doc) : null))
                .addOnFailureListener(fallo::onFallo);
    }

    /** Reservas del cliente en tiempo real, la más reciente primero (RF del cliente: historial). */
    public ListenerRegistration escucharDeCliente(@NonNull String clienteId,
                                                  @NonNull Callbacks.Exito<List<Reserva>> cambio,
                                                  @NonNull Callbacks.Fallo fallo) {
        return reservas().whereEqualTo(Reserva.CAMPO_CLIENTE_ID, clienteId)
                .addSnapshotListener((snapshot, error) -> entregar(snapshot, error, cambio, fallo));
    }

    /** Reservas del hotel en tiempo real (admin de hotel: checkouts, chats, reportes). */
    public ListenerRegistration escucharDeHotel(@NonNull String hotelId,
                                                @NonNull Callbacks.Exito<List<Reserva>> cambio,
                                                @NonNull Callbacks.Fallo fallo) {
        return reservas().whereEqualTo(Reserva.CAMPO_HOTEL_ID, hotelId)
                .addSnapshotListener((snapshot, error) -> entregar(snapshot, error, cambio, fallo));
    }

    /** Reservas del hotel, una sola lectura (reportes). */
    public void obtenerDeHotel(@NonNull String hotelId, @NonNull Callbacks.Exito<List<Reserva>> exito,
                               @NonNull Callbacks.Fallo fallo) {
        reservas().whereEqualTo(Reserva.CAMPO_HOTEL_ID, hotelId).get()
                .addOnSuccessListener(snapshot -> exito.onExito(convertirLista(snapshot)))
                .addOnFailureListener(fallo::onFallo);
    }

    /** Todas las reservas (reporte del Superadmin por hotel). */
    public void obtenerTodas(@NonNull Callbacks.Exito<List<Reserva>> exito, @NonNull Callbacks.Fallo fallo) {
        reservas().get()
                .addOnSuccessListener(snapshot -> exito.onExito(convertirLista(snapshot)))
                .addOnFailureListener(fallo::onFallo);
    }

    /** Valoraciones del hotel, la más reciente primero (Opiniones del cliente y Valoraciones del admin). */
    public void obtenerValoraciones(@NonNull String hotelId, @NonNull Callbacks.Exito<List<ValoracionHotel>> exito,
                                    @NonNull Callbacks.Fallo fallo) {
        db.collection(ValoracionHotel.COLECCION).whereEqualTo(ValoracionHotel.CAMPO_HOTEL_ID, hotelId).get()
                .addOnSuccessListener(snapshot -> {
                    List<ValoracionHotel> lista = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        ValoracionHotel v = HotelRepository.convertirSeguro(doc, ValoracionHotel.class);
                        if (v != null) lista.add(v);
                    }
                    lista.sort((a, b) -> {
                        if (a.getFecha() == null || b.getFecha() == null) return 0;
                        return b.getFecha().compareTo(a.getFecha());
                    });
                    exito.onExito(lista);
                })
                .addOnFailureListener(fallo::onFallo);
    }

    // ---------- Checkout ----------

    /**
     * Checkout del cliente (RF-CL-04): guarda su valoración, deja el checkout PENDIENTE para que el
     * hotel cobre y recalcula la calificación promedio del hotel. Todo en una transacción. Devuelve
     * false si la reserva ya no permitía checkout.
     */
    public void hacerCheckout(@NonNull String reservaId, @NonNull String clienteId, double calificacion,
                              @NonNull String comentario, @NonNull Callbacks.Exito<Boolean> exito,
                              @NonNull Callbacks.Fallo fallo) {
        DocumentReference ref = reservas().document(reservaId);
        db.runTransaction(tx -> {
            Reserva reserva = convertir(tx.get(ref));
            if (reserva == null || !clienteId.equals(reserva.getClienteId())
                    || !reserva.puedeHacerCheckout(LocalDate.now())) {
                return false;
            }
            DocumentReference refHotel = db.collection(Hotel.COLECCION).document(reserva.getHotelId());
            Hotel hotel = HotelRepository.convertirSeguro(tx.get(refHotel), Hotel.class);
            int n = hotel != null ? hotel.getNumValoraciones() : 0;
            double promedio = hotel != null && n > 0 ? hotel.getCalificacion() : 0;
            double nuevoPromedio = (promedio * n + calificacion) / (n + 1);

            Map<String, Object> cambios = new HashMap<>();
            cambios.put(Reserva.CAMPO_ESTADO, EstadoReserva.EN_CURSO.name());
            cambios.put(Reserva.CAMPO_ESTADO_CHECKOUT, EstadoCheckout.PENDIENTE.name());
            cambios.put(Reserva.CAMPO_VALORACION, calificacion);
            cambios.put(Reserva.CAMPO_COMENTARIO, comentario);
            cambios.put(Reserva.CAMPO_FECHA_CHECKOUT, FieldValue.serverTimestamp());
            tx.update(ref, cambios);

            tx.set(db.collection(ValoracionHotel.COLECCION).document(reservaId),
                    ValoracionHotel.desde(reserva, calificacion, comentario));

            Map<String, Object> cambiosHotel = new HashMap<>();
            cambiosHotel.put(Hotel.CAMPO_CALIFICACION, Math.round(nuevoPromedio * 10) / 10.0);
            cambiosHotel.put(Hotel.CAMPO_NUM_VALORACIONES, n + 1);
            tx.update(refHotel, cambiosHotel);
            return true;
        }).addOnSuccessListener(exito::onExito).addOnFailureListener(fallo::onFallo);
    }

    /**
     * El cliente pidió el taxi gratuito con esta reserva: se guarda el id del servicio para no
     * pedirlo dos veces y para volver al seguimiento desde Mis reservas.
     */
    public void marcarTaxiSolicitado(@NonNull String reservaId, @NonNull String servicioTaxiId,
                                     @NonNull Callbacks.Exito<Void> exito, @NonNull Callbacks.Fallo fallo) {
        Map<String, Object> cambios = new HashMap<>();
        cambios.put(Reserva.CAMPO_TAXI_SOLICITADO, true);
        cambios.put(Reserva.CAMPO_SERVICIO_TAXI_ID, servicioTaxiId);
        reservas().document(reservaId).update(cambios)
                .addOnSuccessListener(unused -> exito.onExito(null))
                .addOnFailureListener(fallo::onFallo);
    }

    /**
     * El hotel cobra el checkout PENDIENTE: total de la reserva más el cobro adicional por daños
     * (RN-012, si lo hay). La reserva queda FINALIZADA. Devuelve la reserva cobrada, o null si ya no
     * estaba pendiente.
     */
    public void procesarCheckout(@NonNull String reservaId, double montoAdicional, @Nullable String motivo,
                                 @Nullable String observacion, @NonNull Callbacks.Exito<Reserva> exito,
                                 @NonNull Callbacks.Fallo fallo) {
        DocumentReference ref = reservas().document(reservaId);
        db.runTransaction(tx -> {
            Reserva reserva = convertir(tx.get(ref));
            if (reserva == null || reserva.getEstadoCheckout() != EstadoCheckout.PENDIENTE) {
                return null;
            }
            double total = reserva.getMontoTotal() + montoAdicional;
            Map<String, Object> cambios = new HashMap<>();
            cambios.put(Reserva.CAMPO_ESTADO, EstadoReserva.FINALIZADA.name());
            cambios.put(Reserva.CAMPO_ESTADO_CHECKOUT, EstadoCheckout.PROCESADO.name());
            cambios.put(Reserva.CAMPO_COBRO_ADICIONAL_MONTO, montoAdicional);
            cambios.put(Reserva.CAMPO_COBRO_ADICIONAL_MOTIVO, motivo);
            cambios.put(Reserva.CAMPO_COBRO_ADICIONAL_OBSERVACION, observacion);
            cambios.put(Reserva.CAMPO_MONTO_COBRADO, total);
            cambios.put(Reserva.CAMPO_FECHA_COBRO, FieldValue.serverTimestamp());
            tx.update(ref, cambios);

            reserva.setEstado(EstadoReserva.FINALIZADA);
            reserva.setEstadoCheckout(EstadoCheckout.PROCESADO);
            reserva.setCobroAdicionalMonto(montoAdicional);
            reserva.setMontoCobrado(total);
            return reserva;
        }).addOnSuccessListener(exito::onExito).addOnFailureListener(fallo::onFallo);
    }

    // ---------- Helpers ----------

    private static void entregar(@Nullable QuerySnapshot snapshot, @Nullable Exception error,
                                 Callbacks.Exito<List<Reserva>> cambio, Callbacks.Fallo fallo) {
        if (error != null) {
            fallo.onFallo(error);
            return;
        }
        cambio.onExito(convertirLista(snapshot));
    }

    /** Más reciente primero (por fecha de entrada, que siempre existe). */
    private static List<Reserva> convertirLista(@Nullable QuerySnapshot snapshot) {
        List<Reserva> lista = new ArrayList<>();
        if (snapshot == null) return lista;
        for (DocumentSnapshot doc : snapshot.getDocuments()) {
            Reserva r = convertir(doc);
            if (r != null) lista.add(r);
        }
        lista.sort((a, b) -> String.valueOf(b.getFechaEntrada()).compareTo(String.valueOf(a.getFechaEntrada())));
        return lista;
    }

    @Nullable
    private static Reserva convertir(@NonNull DocumentSnapshot doc) {
        if (!doc.exists()) return null;
        try {
            return doc.toObject(Reserva.class);
        } catch (RuntimeException e) {
            Log.e(TAG, "reservas/" + doc.getId() + " tiene un formato inválido y se ignora", e);
            return null;
        }
    }
}
