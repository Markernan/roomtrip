package com.example.roomtrip.data.model;

import java.io.Serializable;

public class Hotel implements Serializable {
    private String nombre;
    private String ubicacion;
    private String contacto;
    // Activo por defecto: los hoteles que ve el cliente (constructor de 5 parámetros) están activos.
    private EstadoCuenta estado = EstadoCuenta.ACTIVO;
    private String precio;
    private float calificacion;
    private int imagenResId;
    private String adminNombre;
    private String adminEmail;

    // 1. Constructor vacío
    public Hotel() {}

    // 2. Constructor para CLIENTE (5 parámetros)
    public Hotel(String nombre, String ubicacion, String precio, float calificacion, int imagenResId) {
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.precio = precio;
        this.calificacion = calificacion;
        this.imagenResId = imagenResId;
    }

    // 3. Constructor para SUPERADMIN (8 parámetros)
    public Hotel(String nombre, String ubicacion, float calificacion, String adminNombre, String adminEmail, String contacto, int imagenResId, boolean activo) {
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.calificacion = calificacion;
        this.adminNombre = adminNombre;
        this.adminEmail = adminEmail;
        this.contacto = contacto;
        this.imagenResId = imagenResId;
        this.estado = EstadoCuenta.desde(activo);
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public String getContacto() { return contacto; }
    public void setContacto(String contacto) { this.contacto = contacto; }

    public EstadoCuenta getEstado() { return estado; }
    public void setEstado(EstadoCuenta estado) { this.estado = estado; }

    public String getPrecio() { return precio; }
    public void setPrecio(String precio) { this.precio = precio; }

    public float getCalificacion() { return calificacion; }
    public void setCalificacion(float calificacion) { this.calificacion = calificacion; }

    public int getImagenResId() { return imagenResId; }
    public void setImagenResId(int imagenResId) { this.imagenResId = imagenResId; }

    public String getAdminNombre() { return adminNombre; }
    public void setAdminNombre(String adminNombre) { this.adminNombre = adminNombre; }

    public String getAdminEmail() { return adminEmail; }
    public void setAdminEmail(String adminEmail) { this.adminEmail = adminEmail; }
}