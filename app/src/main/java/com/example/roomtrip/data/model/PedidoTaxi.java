package com.example.roomtrip.data.model;

import java.io.Serializable;

/**
 * Pedido de traslado en estado SOLICITADO, visible para los taxistas (RF-TAX-005).
 * Serializable para pasarlo por Intent a la pantalla "En camino".
 */
public class PedidoTaxi implements Serializable {

    private final String nombrePasajero;
    private final float calificacionPasajero;
    private final String origen;
    private final String destino;
    private final int minutosAlEncuentro;

    public PedidoTaxi(String nombrePasajero, float calificacionPasajero, String origen,
                      String destino, int minutosAlEncuentro) {
        this.nombrePasajero = nombrePasajero;
        this.calificacionPasajero = calificacionPasajero;
        this.origen = origen;
        this.destino = destino;
        this.minutosAlEncuentro = minutosAlEncuentro;
    }

    public String getNombrePasajero() { return nombrePasajero; }
    public float getCalificacionPasajero() { return calificacionPasajero; }
    public String getOrigen() { return origen; }
    public String getDestino() { return destino; }
    public int getMinutosAlEncuentro() { return minutosAlEncuentro; }
}
