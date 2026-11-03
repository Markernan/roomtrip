package com.example.roomtrip.data.firebase;

import androidx.annotation.NonNull;

import com.example.roomtrip.data.model.MensajeChat;
import com.example.roomtrip.data.model.Reserva;
import com.example.roomtrip.data.model.RolUsuario;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Chat privado cliente–hotel en {@code reservas/{reservaId}/mensajes}. Va en Firestore (no en Realtime
 * Database) para usar una sola base; los mensajes llegan en tiempo real con {@code addSnapshotListener}.
 * Las reglas solo dejan escribir mientras la reserva está activa (RN-006).
 */
public class ChatRepository {

    private final FirebaseFirestore db;

    public ChatRepository() {
        db = FirebaseFirestore.getInstance();
    }

    private CollectionReference mensajes(String reservaId) {
        return db.collection(Reserva.COLECCION).document(reservaId).collection(Reserva.SUBCOLECCION_MENSAJES);
    }

    /** Mensajes en orden de envío (orderBy de un solo campo: no requiere índice compuesto). */
    public ListenerRegistration escuchar(@NonNull String reservaId, @NonNull Callbacks.Exito<List<MensajeChat>> cambio,
                                         @NonNull Callbacks.Fallo fallo) {
        return mensajes(reservaId).orderBy(MensajeChat.CAMPO_FECHA, Query.Direction.ASCENDING)
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null) {
                        fallo.onFallo(error);
                        return;
                    }
                    List<MensajeChat> lista = new ArrayList<>();
                    if (snapshot != null) {
                        for (DocumentSnapshot doc : snapshot.getDocuments()) {
                            MensajeChat m = HotelRepository.convertirSeguro(doc, MensajeChat.class);
                            if (m != null) lista.add(m);
                        }
                    }
                    cambio.onExito(lista);
                });
    }

    public void enviar(@NonNull String reservaId, @NonNull String texto, @NonNull String emisorId,
                       @NonNull RolUsuario rolEmisor, @NonNull Callbacks.Exito<Void> exito,
                       @NonNull Callbacks.Fallo fallo) {
        mensajes(reservaId).add(new MensajeChat(texto, emisorId, rolEmisor))
                .addOnSuccessListener(ref -> exito.onExito(null))
                .addOnFailureListener(fallo::onFallo);
    }
}
