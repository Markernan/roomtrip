package com.roomtrip.app.superadmin;

public class LogEvento {
    private String titulo;
    private String fecha;
    private String hora;
    private String detalle;
    private int iconResId;

    public LogEvento(String titulo, String fecha, String hora, String detalle, int iconResId) {
        this.titulo = titulo;
        this.fecha = fecha;
        this.hora = hora;
        this.detalle = detalle;
        this.iconResId = iconResId;
    }

    public String getTitulo() { return titulo; }
    public String getFecha() { return fecha; }
    public String getHora() { return hora; }
    public String getDetalle() { return detalle; }
    public int getIconResId() { return iconResId; }
}
