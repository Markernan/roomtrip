package com.example.roomtrip.data.model;


import com.google.firebase.firestore.DocumentId;

/** Habitación de un hotel. En Firestore: {@code hoteles/{hotelId}/habitaciones/{id}}. */
public class Habitacion {
    @DocumentId
    private String id;
    private int areaM2;
    private String tipo;
    private String capacidad;
    private double precio;
    private EstadoHabitacion estado;

    public Habitacion() {
    }

    public Habitacion(String tipo, String capacidad, double precio, EstadoHabitacion estado) {
        this.tipo = tipo;
        this.capacidad = capacidad;
        this.precio = precio;
        this.estado = estado;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public int getAreaM2() { return areaM2; }
    public void setAreaM2(int areaM2) { this.areaM2 = areaM2; }

    public String getTipo() { return tipo; }
    public String getCapacidad() { return capacidad; }
    public double getPrecio() { return precio; }
    public EstadoHabitacion getEstado() { return estado; }

    public void setTipo(String tipo) { this.tipo = tipo; }
    public void setCapacidad(String capacidad) { this.capacidad = capacidad; }
    public void setPrecio(double precio) { this.precio = precio; }
    public void setEstado(EstadoHabitacion estado) { this.estado = estado; }
}
