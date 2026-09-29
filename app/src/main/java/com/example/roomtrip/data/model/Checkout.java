package com.example.roomtrip.data.model;

public class Checkout {
    private String nombreHuesped;
    private String habitacion;
    private String horaLimite;
    private String estado;
    private double monto;

    public Checkout(String nombreHuesped, String habitacion, String horaLimite, String estado) {
        this(nombreHuesped, habitacion, horaLimite, estado, 0.0);
    }

    public Checkout(String nombreHuesped, String habitacion, String horaLimite, String estado, double monto) {
        this.nombreHuesped = nombreHuesped;
        this.habitacion = habitacion;
        this.horaLimite = horaLimite;
        this.estado = estado;
        this.monto = monto;
    }

    public String getNombreHuesped() { return nombreHuesped; }
    public String getHabitacion() { return habitacion; }
    public String getHoraLimite() { return horaLimite; }
    public String getEstado() { return estado; }
    public double getMonto() { return monto; }

    public String getIniciales() {
        if (nombreHuesped == null || nombreHuesped.trim().isEmpty()) return "??";
        String[] partes = nombreHuesped.trim().split("\\s+");
        if (partes.length >= 2) {
            return (partes[0].substring(0, 1) + partes[1].substring(0, 1)).toUpperCase();
        } else {
            return partes[0].substring(0, Math.min(2, partes[0].length())).toUpperCase();
        }
    }
}