package com.example.roomtrip.data.model;

import java.io.Serializable;

public class Mensaje implements Serializable {
    public String texto;
    public String hora;
    public boolean esMio;

    public Mensaje() {}

    public Mensaje(String texto, String hora, boolean esMio) {
        this.texto = texto;
        this.hora = hora;
        this.esMio = esMio;
    }
}