package com.roomtrip.app.cliente;

/** Modelo local temporal. Se reemplaza por Realtime Database en el Lab 6 (RF-GN-04: fecha y hora). */
public class Mensaje {
    public final String texto;
    public final String hora;
    public final boolean esMio;

    public Mensaje(String texto, String hora, boolean esMio) {
        this.texto = texto;
        this.hora = hora;
        this.esMio = esMio;
    }
}
