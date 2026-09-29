package com.example.roomtrip.data.model;

public class Chat {
    private String nombreHuesped;
    private String habitacion;
    private String ultimoMensaje;
    private String hora;
    private int mensajesSinLeer;

    public Chat(String nombreHuesped, String habitacion, String ultimoMensaje, String hora, int mensajesSinLeer) {
        this.nombreHuesped = nombreHuesped;
        this.habitacion = habitacion;
        this.ultimoMensaje = ultimoMensaje;
        this.hora = hora;
        this.mensajesSinLeer = mensajesSinLeer;
    }

    public String getNombreHuesped() { return nombreHuesped; }
    public String getHabitacion() { return habitacion; }
    public String getUltimoMensaje() { return ultimoMensaje; }
    public String getHora() { return hora; }
    public int getMensajesSinLeer() { return mensajesSinLeer; }
}