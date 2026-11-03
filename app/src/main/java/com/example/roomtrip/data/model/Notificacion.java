package com.example.roomtrip.data.model;

public class Notificacion {
    public enum TipoNotificacion {
        RESERVA,   // Nueva reserva
        PAGO,      // Pago confirmado
        CANCELACION // Cancelación de reserva
    }

    private String titulo;
    private String mensaje;
    private String hora;
    private TipoNotificacion tipo;
    private boolean leida;

    public Notificacion(String titulo, String mensaje, String hora, TipoNotificacion tipo, boolean leida) {
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.hora = hora;
        this.tipo = tipo;
        this.leida = leida;
    }

    public String getTitulo() { return titulo; }
    public String getMensaje() { return mensaje; }
    public String getHora() { return hora; }
    public TipoNotificacion getTipo() { return tipo; }
    public boolean isLeida() { return leida; }
    public void setLeida(boolean leida) { this.leida = leida; }
}