package com.example.roomtrip.data.model;

import com.google.firebase.firestore.DocumentId;

/** Servicio que ofrece un hotel (wifi, desayuno...). En Firestore: {@code hoteles/{hotelId}/servicios/{id}}. */
public class Servicio {
    @DocumentId
    private String id;
    private String descripcion;
    private String nombre;
    private String categoria; // Ej: "Alimentos", "Bienestar", "Limpieza", "Tours"
    private double precio;
    private boolean disponible;

    public Servicio() {
    }

    public Servicio(String nombre, String categoria, double precio, boolean disponible) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.disponible = disponible;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getNombre() { return nombre; }
    public String getCategoria() { return categoria; }
    public double getPrecio() { return precio; }
    public boolean isDisponible() { return disponible; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public void setPrecio(double precio) { this.precio = precio; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
}
