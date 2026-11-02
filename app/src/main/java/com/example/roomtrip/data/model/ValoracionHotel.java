package com.example.roomtrip.data.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Documento de la colección {@code valoraciones}: la valoración que el cliente deja en el checkout
 * (RF-CL-04). El id es el de la reserva, así hay una sola por reserva. Cualquier usuario autenticado
 * la puede leer (son las "Opiniones" del hotel).
 */
public class ValoracionHotel {

    public static final String COLECCION = "valoraciones";
    public static final String CAMPO_HOTEL_ID = "hotelId";

    @DocumentId
    private String id;
    private String hotelId;
    private String clienteId;
    private String clienteNombre;
    private String habitacionTipo;
    private double calificacion;
    private String comentario;
    @ServerTimestamp
    private Date fecha;

    public ValoracionHotel() {
    }

    public static ValoracionHotel desde(Reserva reserva, double calificacion, String comentario) {
        ValoracionHotel v = new ValoracionHotel();
        v.hotelId = reserva.getHotelId();
        v.clienteId = reserva.getClienteId();
        v.clienteNombre = reserva.getClienteNombre();
        v.habitacionTipo = reserva.getHabitacionTipo();
        v.calificacion = calificacion;
        v.comentario = comentario;
        return v;
    }

    /** Modelo de vista que usan los adapters de valoraciones del admin de hotel. */
    public Valoracion aVista() {
        String textoFecha = fecha != null
                ? new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(fecha) : "";
        return new Valoracion(clienteNombre, habitacionTipo, textoFecha, (float) calificacion, comentario);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getHotelId() { return hotelId; }
    public void setHotelId(String hotelId) { this.hotelId = hotelId; }
    public String getClienteId() { return clienteId; }
    public void setClienteId(String clienteId) { this.clienteId = clienteId; }
    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }
    public String getHabitacionTipo() { return habitacionTipo; }
    public void setHabitacionTipo(String habitacionTipo) { this.habitacionTipo = habitacionTipo; }
    public double getCalificacion() { return calificacion; }
    public void setCalificacion(double calificacion) { this.calificacion = calificacion; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
}
