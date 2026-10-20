package com.example.roomtrip.data.model;

public class Habitacion {
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

    public String getTipo() { return tipo; }
    public String getCapacidad() { return capacidad; }
    public double getPrecio() { return precio; }
    public EstadoHabitacion getEstado() { return estado; }

    public void setTipo(String tipo) { this.tipo = tipo; }
    public void setCapacidad(String capacidad) { this.capacidad = capacidad; }
    public void setPrecio(double precio) { this.precio = precio; }
    public void setEstado(EstadoHabitacion estado) { this.estado = estado; }
}
