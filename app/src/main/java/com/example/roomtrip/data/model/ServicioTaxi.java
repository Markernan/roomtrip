package com.example.roomtrip.data.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Documento de la colección {@code serviciosTaxi} en Firestore: el traslado gratuito del hotel al
 * aeropuerto. Un "pedido" que ve el taxista es simplemente un servicio en estado
 * {@link EstadoServicioTaxi#SOLICITADO} sin taxista asignado (RF-TAX-005).
 *
 * <p>POJO para {@code toObject()} y {@code add()} (Clase 9.2): constructor vacío y
 * getters/setters. El {@code estado} se guarda por el nombre del enum ({@code "EN_CAMINO"}), el
 * mismo que usa la API de taxistas. Los métodos de reglas de negocio no empiezan con
 * {@code get}/{@code is}, para que Firestore no los guarde como campos.
 */
public class ServicioTaxi {

    public static final String COLECCION = "serviciosTaxi";

    // Nombres de los campos que se consultan o se actualizan sueltos (transacciones y queries)
    public static final String CAMPO_ESTADO = "estado";
    public static final String CAMPO_TAXISTA_ID = "taxistaId";
    public static final String CAMPO_HOTEL_ID = "hotelId";
    public static final String CAMPO_TAXISTA_NOMBRE = "taxistaNombre";
    public static final String CAMPO_FECHA_ASIGNACION = "fechaAsignacion";
    public static final String CAMPO_FECHA_ACTUALIZACION = "fechaActualizacion";
    public static final String CAMPO_FECHA_FINALIZACION = "fechaFinalizacion";

    /** El id del documento: también es el contenido del código QR que muestra el cliente (RN-011). */
    @DocumentId
    private String id;

    private EstadoServicioTaxi estado;

    private String clienteId;
    private String clienteNombre;
    private double calificacionCliente;

    private String reservaId;       // la reserva cuyo checkout dio derecho al taxi
    private String hotelId;
    private String hotelNombre;     // origen del traslado
    private String aeropuerto;      // destino que indica el cliente
    private int minutosAlEncuentro;

    private String taxistaId;       // uid de Firebase Auth del taxista; null mientras está SOLICITADO
    private String taxistaNombre;

    @ServerTimestamp
    private Date fechaSolicitud;
    private Date fechaAsignacion;
    private Date fechaActualizacion;
    private Date fechaFinalizacion;

    /** Requerido por Firestore para {@code toObject()}. */
    public ServicioTaxi() {
    }

    /** Pedido nuevo, tal como lo crea el cliente al pedir el taxi en el checkout. */
    public static ServicioTaxi nuevaSolicitud(String clienteId, String clienteNombre, double calificacionCliente,
                                              String hotelId, String hotelNombre, String aeropuerto,
                                              int minutosAlEncuentro) {
        ServicioTaxi s = new ServicioTaxi();
        s.estado = EstadoServicioTaxi.SOLICITADO;
        s.clienteId = clienteId;
        s.clienteNombre = clienteNombre;
        s.calificacionCliente = calificacionCliente;
        s.hotelId = hotelId;
        s.hotelNombre = hotelNombre;
        s.aeropuerto = aeropuerto;
        s.minutosAlEncuentro = minutosAlEncuentro;
        return s;
    }

    // ---------- Reglas de negocio (no son getters: no se guardan en Firestore) ----------

    /** Exclusividad del pedido: solo se acepta si sigue SOLICITADO y nadie lo tomó. */
    public boolean puedeSerAceptado() {
        return estado == EstadoServicioTaxi.SOLICITADO && taxistaId == null;
    }

    /** ASIGNADO, EN_CAMINO o EN_TRASLADO: el taxista está ocupado con este servicio. */
    public boolean estaEnCurso() {
        return estado == EstadoServicioTaxi.ASIGNADO
                || estado == EstadoServicioTaxi.EN_CAMINO
                || estado == EstadoServicioTaxi.EN_TRASLADO;
    }

    /** El servicio está asignado a ese taxista. */
    public boolean esDelTaxista(String uidTaxista) {
        return uidTaxista != null && uidTaxista.equals(taxistaId);
    }

    /**
     * Siguiente estado que el taxista puede marcar con un botón (RN-010): ASIGNADO → EN_CAMINO →
     * EN_TRASLADO. Devuelve {@code null} si no hay paso manual: FINALIZADO solo se registra
     * validando el QR del cliente (RN-011), con {@link #qrValido(String)}.
     */
    public EstadoServicioTaxi siguienteEstadoManual() {
        if (estado == null) return null;
        EstadoServicioTaxi siguiente = estado.siguiente();
        if (siguiente == null
                || siguiente == EstadoServicioTaxi.ASIGNADO       // eso lo hace "aceptar pedido"
                || siguiente == EstadoServicioTaxi.FINALIZADO) {  // eso lo hace el QR
            return null;
        }
        return estado.puedePasarA(siguiente) ? siguiente : null;
    }

    /** RN-011: el QR leído corresponde a este servicio y el pasajero ya va en el taxi. */
    public boolean qrValido(String codigoLeido) {
        return estado == EstadoServicioTaxi.EN_TRASLADO
                && id != null
                && codigoLeido != null
                && id.equals(codigoLeido.trim());
    }

    // ---------- Getters y setters (Firestore) ----------

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public EstadoServicioTaxi getEstado() { return estado; }
    public void setEstado(EstadoServicioTaxi estado) { this.estado = estado; }

    public String getClienteId() { return clienteId; }
    public void setClienteId(String clienteId) { this.clienteId = clienteId; }

    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }

    public double getCalificacionCliente() { return calificacionCliente; }
    public void setCalificacionCliente(double calificacionCliente) { this.calificacionCliente = calificacionCliente; }

    public String getReservaId() { return reservaId; }
    public void setReservaId(String reservaId) { this.reservaId = reservaId; }

    public String getHotelId() { return hotelId; }
    public void setHotelId(String hotelId) { this.hotelId = hotelId; }

    public String getHotelNombre() { return hotelNombre; }
    public void setHotelNombre(String hotelNombre) { this.hotelNombre = hotelNombre; }

    public String getAeropuerto() { return aeropuerto; }
    public void setAeropuerto(String aeropuerto) { this.aeropuerto = aeropuerto; }

    public int getMinutosAlEncuentro() { return minutosAlEncuentro; }
    public void setMinutosAlEncuentro(int minutosAlEncuentro) { this.minutosAlEncuentro = minutosAlEncuentro; }

    public String getTaxistaId() { return taxistaId; }
    public void setTaxistaId(String taxistaId) { this.taxistaId = taxistaId; }

    public String getTaxistaNombre() { return taxistaNombre; }
    public void setTaxistaNombre(String taxistaNombre) { this.taxistaNombre = taxistaNombre; }

    public Date getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(Date fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }

    public Date getFechaAsignacion() { return fechaAsignacion; }
    public void setFechaAsignacion(Date fechaAsignacion) { this.fechaAsignacion = fechaAsignacion; }

    public Date getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(Date fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    public Date getFechaFinalizacion() { return fechaFinalizacion; }
    public void setFechaFinalizacion(Date fechaFinalizacion) { this.fechaFinalizacion = fechaFinalizacion; }
}
