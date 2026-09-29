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

    /** "María Fernández" -> "MF"; con una sola palabra, su inicial; sin nombre, cadena vacía. */
    public String getInicialesUsuario() {
        if (usuario == null || usuario.trim().isEmpty()) return "";
        String[] partes = usuario.trim().split("\\s+");
        String iniciales = partes[0].substring(0, 1);
        if (partes.length > 1) iniciales += partes[1].substring(0, 1);
        return iniciales.toUpperCase();
    }
}