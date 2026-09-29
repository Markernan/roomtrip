package com.example.roomtrip.data.model;

public class Servicio {
    private String nombre;
    private String categoria; // Ej: "Alimentos", "Bienestar", "Limpieza", "Tours"
    private double precio;
    private boolean disponible;

    public Servicio(String nombre, String categoria, double precio, boolean disponible) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.disponible = disponible;
    }

    public String getNombre() { return nombre; }
    public String getCategoria() { return categoria; }
    public double getPrecio() { return precio; }
    public boolean isDisponible() { return disponible; }
}