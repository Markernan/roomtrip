package com.example.roomtrip.data.model;

import java.io.Serializable;

/**
 * Modelo unificado de Hotel para toda la aplicación.
 * Compatible con Cliente, Admin de Hotel, SuperAdmin y la capa de persistencia (Room / Firestore).
 */
public class Hotel implements Serializable {

    // Atributos públicos / directos para compatibilidad con vistas de Cliente
    public String nombre;
    public String ubicacion;
    public String precioPorNoche;
    public float calificacion;
    public int fotoResId;

    // Atributos para SuperAdmin / Admin
    private String adminName;
    private String adminEmail;
    private String adminPhone;
    private String photoUrl;
    private boolean isActive = true;

    // Constructor vacío requerido para Firestore / Room / JSON
    public Hotel() {}

    // Constructor para módulo Cliente
    public Hotel(String nombre, String ubicacion, String precioPorNoche, float calificacion, int fotoResId) {
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.precioPorNoche = precioPorNoche;
        this.calificacion = calificacion;
        this.fotoResId = fotoResId;
    }

    // Constructor completo para módulo SuperAdmin / Admin
    public Hotel(String name, String location, float rating, String adminName, String adminEmail, String adminPhone, int photoResId, boolean isActive) {
        this.nombre = name;
        this.ubicacion = location;
        this.calificacion = rating;
        this.adminName = adminName;
        this.adminEmail = adminEmail;
        this.adminPhone = adminPhone;
        this.fotoResId = photoResId;
        this.isActive = isActive;
    }

    // Getters y Setters unificados
    public String getName() { return nombre != null ? nombre : ""; }
    public void setName(String name) { this.nombre = name; }

    public String getLocation() { return ubicacion != null ? ubicacion : ""; }
    public void setLocation(String location) { this.ubicacion = location; }

    public float getRating() { return calificacion; }
    public void setRating(float rating) { this.calificacion = rating; }

    public String getPrecioPorNoche() { return precioPorNoche; }
    public void setPrecioPorNoche(String precioPorNoche) { this.precioPorNoche = precioPorNoche; }

    public String getAdminName() { return adminName != null ? adminName : ""; }
    public void setAdminName(String adminName) { this.adminName = adminName; }

    public String getAdminEmail() { return adminEmail; }
    public void setAdminEmail(String adminEmail) { this.adminEmail = adminEmail; }

    public String getAdminPhone() { return adminPhone; }
    public void setAdminPhone(String adminPhone) { this.adminPhone = adminPhone; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public int getPhotoResId() { return fotoResId; }
    public void setPhotoResId(int photoResId) { this.fotoResId = photoResId; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}