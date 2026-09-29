package com.example.roomtrip.data.model;

public class Habitacion {
    private String tipo;
    private String capacidad;
    private double precio;
    private String estado;

    public Habitacion(String tipo, String capacidad, double precio, String estado) {
        this.tipo = tipo;
        this.capacidad = capacidad;
        this.precio = precio;
        this.estado = estado;
    }

    public String getTipo() { return tipo; }
    public String getCapacidad() { return capacidad; }
    public double getPrecio() { return precio; }
    public String getEstado() { return estado; }
}