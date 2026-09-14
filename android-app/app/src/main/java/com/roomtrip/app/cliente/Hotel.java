package com.roomtrip.app.cliente;

/** Modelo local temporal, sin persistencia. Se reemplaza por Firestore en el Lab 6. */
public class Hotel {
    public final String nombre;
    public final String ubicacion;
    public final String precioPorNoche;
    public final float calificacion;
    public final int fotoResId;

    public Hotel(String nombre, String ubicacion, String precioPorNoche, float calificacion, int fotoResId) {
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.precioPorNoche = precioPorNoche;
        this.calificacion = calificacion;
        this.fotoResId = fotoResId;
    }
}
