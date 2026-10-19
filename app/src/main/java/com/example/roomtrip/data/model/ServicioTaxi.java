package com.example.roomtrip.data.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Servicio de taxi que el taxista aceptó: el pedido, su estado actual (RN-010) y la fecha/hora en
 * que se aceptó o finalizó. Es lo que se guarda en JSON en el almacenamiento interno (Lab 5).
 */
public class ServicioTaxi {

    private final PedidoTaxi pedido;
    private EstadoServicioTaxi estado;
    private long fechaHora;          // epoch millis: cuándo se aceptó / finalizó

    public ServicioTaxi(PedidoTaxi pedido) {
        this(pedido, EstadoServicioTaxi.SOLICITADO, System.currentTimeMillis());
    }

    private ServicioTaxi(PedidoTaxi pedido, EstadoServicioTaxi estado, long fechaHora) {
        this.pedido = pedido;
        this.estado = estado;
        this.fechaHora = fechaHora;
    }

    public PedidoTaxi getPedido() { return pedido; }
    public EstadoServicioTaxi getEstado() { return estado; }
    public long getFechaHora() { return fechaHora; }

    public void setFechaHora(long fechaHora) { this.fechaHora = fechaHora; }

    /** RN-010: pasa al estado inmediato siguiente, sin saltarse ninguno. */
    public void avanzarEstado() {
        EstadoServicioTaxi siguiente = estado.siguiente();
        if (estado.puedePasarA(siguiente)) {
            estado = siguiente;
        }
    }

    // ---------- JSON (org.json) ----------

    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("nombrePasajero", pedido.getNombrePasajero());
        json.put("calificacionPasajero", pedido.getCalificacionPasajero());
        json.put("origen", pedido.getOrigen());
        json.put("destino", pedido.getDestino());
        json.put("minutosAlEncuentro", pedido.getMinutosAlEncuentro());
        // El enum se guarda por su nombre ("EN_CAMINO") y se lee con valueOf
        json.put("estado", estado.name());
        json.put("fechaHora", fechaHora);
        return json;
    }

    public static ServicioTaxi fromJson(JSONObject json) throws JSONException {
        PedidoTaxi pedido = new PedidoTaxi(
                json.getString("nombrePasajero"),
                (float) json.getDouble("calificacionPasajero"),
                json.getString("origen"),
                json.getString("destino"),
                json.getInt("minutosAlEncuentro"));
        return new ServicioTaxi(pedido,
                EstadoServicioTaxi.valueOf(json.getString("estado")),
                json.getLong("fechaHora"));
    }
}
