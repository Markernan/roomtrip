package com.example.roomtrip.data.model;

public class Chat {
    private String nombreHuesped;
    private String habitacion;
    private String ultimoMensaje;
    private String hora;
    private int mensajesSinLeer;
    // Id de la reserva en Firestore (Lab 6): el chat vive en reservas/{reservaId}/mensajes
    private String reservaId;

    public Chat(String nombreHuesped, String habitacion, String ultimoMensaje, String hora, int mensajesSinLeer) {
        this.nombreHuesped = nombreHuesped;
        this.habitacion = habitacion;
        this.ultimoMensaje = ultimoMensaje;
        this.hora = hora;
        this.mensajesSinLeer = mensajesSinLeer;
    }

    public String getReservaId() { return reservaId; }
    public void setReservaId(String reservaId) { this.reservaId = reservaId; }

    public String getNombreHuesped() { return nombreHuesped; }
    public String getHabitacion() { return habitacion; }
    public String getUltimoMensaje() { return ultimoMensaje; }
    public String getHora() { return hora; }
    public int getMensajesSinLeer() { return mensajesSinLeer; }
}