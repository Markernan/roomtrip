package com.example.roomtrip.data.firebase;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.roomtrip.data.MockData;
import com.example.roomtrip.data.model.EstadoCuenta;
import com.example.roomtrip.data.model.Habitacion;
import com.example.roomtrip.data.model.Hotel;
import com.example.roomtrip.data.model.Servicio;
import com.example.roomtrip.utils.ImagenesHotel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Hoteles en {@code hoteles/{id}} y sus subcolecciones {@code habitaciones} y {@code servicios}.
 */
public class HotelRepository {

    private static final String TAG = "HotelRepository";

    private final FirebaseFirestore db;

    public HotelRepository() {
        db = FirebaseFirestore.getInstance();
    }

    private CollectionReference hoteles() {
        return db.collection(Hotel.COLECCION);
    }

    private CollectionReference habitaciones(String hotelId) {
        return hoteles().document(hotelId).collection(Hotel.SUBCOLECCION_HABITACIONES);
    }

    private CollectionReference servicios(String hotelId) {
        return hoteles().document(hotelId).collection(Hotel.SUBCOLECCION_SERVICIOS);
    }

    // ---------- Hoteles ----------

    /** Hoteles que ve el cliente: solo los ACTIVOS, mejor calificados primero. */
    public void obtenerActivos(@NonNull Callbacks.Exito<List<Hotel>> exito, @NonNull Callbacks.Fallo fallo) {
        hoteles().whereEqualTo(Hotel.CAMPO_ESTADO, EstadoCuenta.ACTIVO.name()).get()
                .addOnSuccessListener(snapshot -> {
                    List<Hotel> lista = convertirLista(snapshot.getDocuments());
                    lista.sort((a, b) -> Float.compare(b.getCalificacion(), a.getCalificacion()));
                    exito.onExito(lista);
                })
                .addOnFailureListener(fallo::onFallo);
    }

    /** Todos los hoteles, por nombre (Superadmin). */
    public void obtenerTodos(@NonNull Callbacks.Exito<List<Hotel>> exito, @NonNull Callbacks.Fallo fallo) {
        hoteles().get()
                .addOnSuccessListener(snapshot -> {
                    List<Hotel> lista = convertirLista(snapshot.getDocuments());
                    lista.sort((a, b) -> String.valueOf(a.getNombre()).compareToIgnoreCase(String.valueOf(b.getNombre())));
                    exito.onExito(lista);
                })
                .addOnFailureListener(fallo::onFallo);
    }

    /** Un hotel por id; null si no existe. */
    public void obtener(@NonNull String hotelId, @NonNull Callbacks.Exito<Hotel> exito, @NonNull Callbacks.Fallo fallo) {
        hoteles().document(hotelId).get()
                .addOnSuccessListener(doc -> exito.onExito(doc.exists() ? convertir(doc) : null))
                .addOnFailureListener(fallo::onFallo);
    }

    /**
     * El Superadmin registra el hotel y asigna su administrador por correo (invitación). Se escriben
     * los dos documentos en un batch: o se guardan ambos o ninguno. Devuelve el id del hotel.
     */
    public void registrar(@NonNull Hotel hotel, @NonNull String correoAdmin,
                          @NonNull Callbacks.Exito<String> exito, @NonNull Callbacks.Fallo fallo) {
        DocumentReference ref = hoteles().document();
        hotel.setEstado(EstadoCuenta.ACTIVO);
        hotel.setAdminEmail(UsuarioRepository.claveCorreo(correoAdmin));

        WriteBatch batch = db.batch();
        batch.set(ref, hotel);
        batch.set(db.collection(UsuarioRepository.COLECCION_INVITACIONES).document(UsuarioRepository.claveCorreo(correoAdmin)),
                UsuarioRepository.datosInvitacion(ref.getId(), hotel.getNombre()));
        batch.commit()
                .addOnSuccessListener(unused -> exito.onExito(ref.getId()))
                .addOnFailureListener(fallo::onFallo);
    }

    /** Actualiza campos sueltos del hotel ({@code update()}). */
    public void actualizar(@NonNull String hotelId, @NonNull Map<String, Object> cambios,
                           @NonNull Callbacks.Exito<Void> exito, @NonNull Callbacks.Fallo fallo) {
        hoteles().document(hotelId).update(cambios)
                .addOnSuccessListener(unused -> exito.onExito(null))
                .addOnFailureListener(fallo::onFallo);
    }

    /** RF-USR-007: el Superadmin activa o desactiva un hotel. */
    public void cambiarEstado(@NonNull String hotelId, @NonNull EstadoCuenta estado,
                              @NonNull Callbacks.Exito<Void> exito, @NonNull Callbacks.Fallo fallo) {
        Map<String, Object> cambios = new HashMap<>();
        cambios.put(Hotel.CAMPO_ESTADO, estado.name());
        actualizar(hotelId, cambios, exito, fallo);
    }

    // ---------- Habitaciones y servicios ----------

    /** Habitaciones del hotel, la más barata primero. */
    public void obtenerHabitaciones(@NonNull String hotelId, @NonNull Callbacks.Exito<List<Habitacion>> exito,
                                    @NonNull Callbacks.Fallo fallo) {
        habitaciones(hotelId).get()
                .addOnSuccessListener(snapshot -> {
                    List<Habitacion> lista = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Habitacion h = convertirSeguro(doc, Habitacion.class);
                        if (h != null) lista.add(h);
                    }
                    lista.sort((a, b) -> Double.compare(a.getPrecio(), b.getPrecio()));
                    exito.onExito(lista);
                })
                .addOnFailureListener(fallo::onFallo);
    }

    /**
     * Agrega una habitación y actualiza el "desde S/X noche" del hotel con la tarifa más baja. Se
     * hace en un batch para que la lista de hoteles del cliente no quede desactualizada.
     */
    public void agregarHabitacion(@NonNull String hotelId, @NonNull Habitacion habitacion, double precioMinimoActual,
                                  @NonNull Callbacks.Exito<Void> exito, @NonNull Callbacks.Fallo fallo) {
        WriteBatch batch = db.batch();
        batch.set(habitaciones(hotelId).document(), habitacion);
        double minimo = precioMinimoActual > 0 ? Math.min(precioMinimoActual, habitacion.getPrecio()) : habitacion.getPrecio();
        batch.update(hoteles().document(hotelId), Hotel.CAMPO_PRECIO, textoPrecio(minimo));
        batch.commit()
                .addOnSuccessListener(unused -> exito.onExito(null))
                .addOnFailureListener(fallo::onFallo);
    }

    public void obtenerServicios(@NonNull String hotelId, @NonNull Callbacks.Exito<List<Servicio>> exito,
                                 @NonNull Callbacks.Fallo fallo) {
        servicios(hotelId).get()
                .addOnSuccessListener(snapshot -> {
                    List<Servicio> lista = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Servicio s = convertirSeguro(doc, Servicio.class);
                        if (s != null) lista.add(s);
                    }
                    lista.sort((a, b) -> String.valueOf(a.getNombre()).compareToIgnoreCase(String.valueOf(b.getNombre())));
                    exito.onExito(lista);
                })
                .addOnFailureListener(fallo::onFallo);
    }

    public void agregarServicio(@NonNull String hotelId, @NonNull Servicio servicio,
                                @NonNull Callbacks.Exito<Void> exito, @NonNull Callbacks.Fallo fallo) {
        servicios(hotelId).add(servicio)
                .addOnSuccessListener(ref -> exito.onExito(null))
                .addOnFailureListener(fallo::onFallo);
    }

    // ---------- Datos de demostración ----------

    /**
     * Crea hoteles de ejemplo (con habitaciones y servicios de MockData) para probar el flujo del
     * cliente. Solo si todavía no hay ningún hotel, para no duplicar. Devuelve cuántos creó (0 si ya
     * había hoteles).
     */
    public void cargarDatosDemo(@NonNull Callbacks.Exito<Integer> exito, @NonNull Callbacks.Fallo fallo) {
        hoteles().get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.getDocuments().isEmpty()) {
                        exito.onExito(0);
                        return;
                    }
                    WriteBatch batch = db.batch();
                    List<Hotel> demo = MockData.getHotelesDemoFirestore();
                    for (Hotel hotel : demo) {
                        DocumentReference ref = hoteles().document();
                        double minimo = Double.MAX_VALUE;
                        for (Habitacion h : MockData.getHabitacionesEjemplo()) {
                            batch.set(ref.collection(Hotel.SUBCOLECCION_HABITACIONES).document(), h);
                            minimo = Math.min(minimo, h.getPrecio());
                        }
                        for (Servicio s : MockData.getServiciosEjemplo()) {
                            batch.set(ref.collection(Hotel.SUBCOLECCION_SERVICIOS).document(), s);
                        }
                        hotel.setPrecio(textoPrecio(minimo));
                        batch.set(ref, hotel);
                    }
                    batch.commit()
                            .addOnSuccessListener(unused -> exito.onExito(demo.size()))
                            .addOnFailureListener(fallo::onFallo);
                })
                .addOnFailureListener(fallo::onFallo);
    }

    // ---------- Helpers ----------

    /** "S/120 noche", el texto que muestran las tarjetas de hotel del cliente. */
    public static String textoPrecio(double precioNoche) {
        return String.format(Locale.US, "S/%.0f noche", precioNoche);
    }

    private static List<Hotel> convertirLista(List<DocumentSnapshot> docs) {
        List<Hotel> lista = new ArrayList<>();
        for (DocumentSnapshot doc : docs) {
            Hotel h = convertir(doc);
            if (h != null) lista.add(h);
        }
        return lista;
    }

    /** El drawable no se guarda en Firestore: se resuelve aquí a partir de la clave {@code imagen}. */
    @Nullable
    static Hotel convertir(@NonNull DocumentSnapshot doc) {
        Hotel hotel = convertirSeguro(doc, Hotel.class);
        if (hotel != null) {
            hotel.setImagenResId(ImagenesHotel.recurso(hotel.getImagen()));
        }
        return hotel;
    }

    @Nullable
    static <T> T convertirSeguro(@NonNull DocumentSnapshot doc, @NonNull Class<T> clase) {
        try {
            return doc.toObject(clase);
        } catch (RuntimeException e) {
            Log.e(TAG, doc.getId() + " no se pudo leer como " + clase.getSimpleName(), e);
            return null;
        }
    }
}
