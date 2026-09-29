package com.example.roomtrip.data.model;

public class Valoracion {
    private String usuario;
    private String detalleHabitacion;
    private String fecha;
    private float calificacion;
    private String comentario;

    public Valoracion(String usuario, String detalleHabitacion, String fecha, float calificacion, String comentario) {
        this.usuario = usuario;
        this.detalleHabitacion = detalleHabitacion;
        this.fecha = fecha;
        this.calificacion = calificacion;
        this.comentario = comentario;
    }

    public String getUsuario() { return usuario; }
    public String getDetalleHabitacion() { return detalleHabitacion; }
    public String getFecha() { return fecha; }
    public float getCalificacion() { return calificacion; }
    public String getComentario() { return comentario; }
}