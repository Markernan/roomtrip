package com.example.roomtrip.data.model;

public class Traslado {
    private String nombreHuesped;
    private String habitacion;
    private String ruta;            // Ej: "Hotel ➔ Aeropuerto"
    private String horaPickup;       // Ej: "14:30 PM"
    private EstadoServicioTaxi estado;
    private String infoConductor;   // Ej: "Pedro V. • ABC-123"
    private double precio;

    public Traslado(String nombreHuesped, String habitacion, String ruta, String horaPickup, EstadoServicioTaxi estado, String infoConductor, double precio) {
        this.nombreHuesped = nombreHuesped;
        this.habitacion = habitacion;
        this.ruta = ruta;
        this.horaPickup = horaPickup;
        this.estado = estado;
        this.infoConductor = infoConductor;
        this.precio = precio;
    }

    public String getNombreHuesped() { return nombreHuesped; }
    public String getHabitacion() { return habitacion; }
    public String getRuta() { return ruta; }
    public String getHoraPickup() { return horaPickup; }
    public EstadoServicioTaxi getEstado() { return estado; }
    public String getInfoConductor() { return infoConductor; }
    public double getPrecio() { return precio; }
}