package com.example.roomtrip.data.firebase;

import androidx.annotation.NonNull;

import com.example.roomtrip.R;
import com.example.roomtrip.data.model.LogEvento;
import com.example.roomtrip.utils.RegistroEventos;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Lectura de la colección {@code logs} para el visor de auditoría del Superadmin. Las escrituras las
 * hace {@link RegistroEventos} desde cada rol.
 */
public class LogRepository {

    /** Cuántos eventos recientes muestra el visor. */
    public static final int LIMITE = 200;

    private final FirebaseFirestore db;

    public LogRepository() {
        db = FirebaseFirestore.getInstance();
    }

    /** Eventos más recientes primero, ya convertidos al modelo de vista de LogAdapter. */
    public void obtenerRecientes(@NonNull Callbacks.Exito<List<LogEvento>> exito, @NonNull Callbacks.Fallo fallo) {
        db.collection(RegistroEventos.COLECCION)
                .orderBy("fecha", Query.Direction.DESCENDING)
                .limit(LIMITE)
                .get()
                .addOnSuccessListener(snapshot -> {
                    SimpleDateFormat dia = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    SimpleDateFormat hora = new SimpleDateFormat("HH:mm:ss 'hrs'", Locale.getDefault());
                    List<LogEvento> lista = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Timestamp ts = doc.getTimestamp("fecha");
                        Date fecha = ts != null ? ts.toDate() : new Date();
                        String tipo = String.valueOf(doc.getString("tipo"));
                        String detalle = doc.getString("detalle");
                        String rol = doc.getString("rol");
                        lista.add(new LogEvento(
                                detalle != null ? detalle : tipo,
                                dia.format(fecha),
                                hora.format(fecha),
                                tipo + (rol != null ? " · " + rol : ""),
                                icono(tipo)));
                    }
                    exito.onExito(lista);
                })
                .addOnFailureListener(fallo::onFallo);
    }

    private static int icono(String tipo) {
        if (tipo.contains("SESION")) return R.drawable.ic_lock;
        if (tipo.contains("TAXI")) return R.drawable.ic_taxi;
        if (tipo.contains("RESERVA") || tipo.contains("CHECKOUT") || tipo.contains("COBRO")) return R.drawable.ic_card;
        if (tipo.contains("HOTEL")) return R.drawable.ic_home_pin;
        return R.drawable.ic_person;
    }
}
