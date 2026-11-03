package com.example.roomtrip.data.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Documento de la colección {@code reservas}: una habitación de un hotel para un rango de noches.
 * Datos del cliente, del hotel y de la habitación van copiados (desnormalizados) para que las
 * listas de cliente y hotel no tengan que leer otros documentos.
 *
 * <p>Las fechas se guardan como texto {@code yyyy-MM-dd}: se ordenan bien como texto y la noche de
 * salida no cuenta (entrada 10, salida 12 = noches 10 y 11).
 *
 * <p>Ciclo: CONFIRMADA (al pagar) → el cliente hace checkout ({@code estadoCheckout = PENDIENTE},
 * {@code estado = EN_CURSO}) → el hotel cobra ({@code estadoCheckout = PROCESADO},
 * {@code estado = FINALIZADA}).
 */
public class Reserva {

    public static final String COLECCION = "reservas";
    public static final String SUBCOLECCION_MENSAJES = "mensajes";

    public static final String CAMPO_CLIENTE_ID = "clienteId";
    public static final String CAMPO_HOTEL_ID = "hotelId";
    public static final String CAMPO_ESTADO = "estado";
    public static final String CAMPO_ESTADO_CHECKOUT = "estadoCheckout";
    public static final String CAMPO_VALORACION = "valoracion";
    public static final String CAMPO_COMENTARIO = "comentario";
    public static final String CAMPO_FECHA_CHECKOUT = "fechaCheckout";
    public static final String CAMPO_TAXI_SOLICITADO = "taxiSolicitado";
    public static final String CAMPO_SERVICIO_TAXI_ID = "servicioTaxiId";
    public static final String CAMPO_COBRO_ADICIONAL_MONTO = "cobroAdicionalMonto";
    public static final String CAMPO_COBRO_ADICIONAL_MOTIVO = "cobroAdicionalMotivo";
    public static final String CAMPO_COBRO_ADICIONAL_OBSERVACION = "cobroAdicionalObservacion";
    public static final String CAMPO_MONTO_COBRADO = "montoCobrado";
    public static final String CAMPO_FECHA_COBRO = "fechaCobro";

    /** Una reserva no puede pasar de este número de noches (límite de la transacción). */
    public static final int MAXIMO_NOCHES = 30;

    @DocumentId
    private String id;

    private String clienteId;
    private String clienteNombre;
    private String hotelId;
    private String hotelNombre;
    private String hotelImagen;
    private String habitacionId;
    private String habitacionTipo;

    private String fechaEntrada;   // yyyy-MM-dd
    private String fechaSalida;    // yyyy-MM-dd (no se cuenta como noche)
    private int noches;
    private double precioNoche;
    private double montoTotal;
    private String tarjetaUltimos4;

    private EstadoReserva estado;
    @ServerTimestamp
    private Date fechaCreacion;

    // Checkout del cliente
    private EstadoCheckout estadoCheckout;
    private double valoracion;
    private String comentario;
    private Date fechaCheckout;
    private boolean taxiSolicitado;
    private String servicioTaxiId;  // id en serviciosTaxi del traslado gratuito, si lo pidió

    // Cobro del hotel (RN-012: el cobro adicional lleva monto, motivo y observación)
    private double cobroAdicionalMonto;
    private String cobroAdicionalMotivo;
    private String cobroAdicionalObservacion;
    private double montoCobrado;
    private Date fechaCobro;

    /** Requerido por Firestore para {@code toObject()}. */
    public Reserva() {
    }

    public static Reserva nueva(String clienteId, String clienteNombre, Hotel hotel, Habitacion habitacion,
                                String fechaEntrada, String fechaSalida, String tarjetaUltimos4) {
        Reserva r = new Reserva();
        r.clienteId = clienteId;
        r.clienteNombre = clienteNombre;
        r.hotelId = hotel.getId();
        r.hotelNombre = hotel.getNombre();
        r.hotelImagen = hotel.getImagen();
        r.habitacionId = habitacion.getId();
        r.habitacionTipo = habitacion.getTipo();
        r.fechaEntrada = fechaEntrada;
        r.fechaSalida = fechaSalida;
        r.noches = contarNoches(fechaEntrada, fechaSalida);
        r.precioNoche = habitacion.getPrecio();
        r.montoTotal = r.noches * habitacion.getPrecio();
        r.tarjetaUltimos4 = tarjetaUltimos4;
        r.estado = EstadoReserva.CONFIRMADA;
        return r;
    }

    // ---------- Fechas (estáticos, probados en ReservaTest) ----------

    /** Noches entre entrada y salida; 0 si las fechas no son válidas o la salida no es posterior. */
    public static int contarNoches(String entrada, String salida) {
        return nochesEntre(entrada, salida).size();
    }

    /** Cada noche ocupada (yyyy-MM-dd), sin incluir el día de salida. Vacía si el rango no es válido. */
    public static List<String> nochesEntre(String entrada, String salida) {
        List<String> noches = new ArrayList<>();
        LocalDate desde = parsear(entrada);
        LocalDate hasta = parsear(salida);
        if (desde == null || hasta == null || !hasta.isAfter(desde)) return noches;
        for (LocalDate d = desde; d.isBefore(hasta) && noches.size() <= MAXIMO_NOCHES; d = d.plusDays(1)) {
            noches.add(d.toString());
        }
        return noches;
    }

    /** RN-001/RN-002: dos rangos [entrada, salida) se cruzan si comparten al menos una noche. */
    public static boolean seSuperponen(String entradaA, String salidaA, String entradaB, String salidaB) {
        List<String> a = nochesEntre(entradaA, salidaA);
        for (String noche : nochesEntre(entradaB, salidaB)) {
            if (a.contains(noche)) return true;
        }
        return false;
    }

    private static LocalDate parsear(String fecha) {
        if (fecha == null) return null;
        try {
            return LocalDate.parse(fecha);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    // ---------- Reglas de negocio (no son getters: no se guardan) ----------

    /** Glosario del ERS: reserva activa = CONFIRMADA o EN_CURSO. */
    public boolean estaActiva() {
        return estado != null && estado.esActiva();
    }

    /** El cliente puede hacer checkout si la reserva está activa, ya empezó y no lo hizo antes. */
    public boolean puedeHacerCheckout(LocalDate hoy) {
        LocalDate entrada = parsear(fechaEntrada);
        return estaActiva() && estadoCheckout == null && entrada != null && !entrada.isAfter(hoy);
    }

    /** RN-006: el chat existe desde la confirmación hasta que el hotel termina el checkout. */
    public boolean chatDisponible() {
        return estaActiva() && estadoCheckout != EstadoCheckout.PROCESADO;
    }

    /** Taxi gratis al aeropuerto (RN-005): la reserva alcanza el monto mínimo del hotel. */
    public boolean calificaParaTaxi(double montoMinimoHotel) {
        return montoTotal >= montoMinimoHotel;
    }

    // ---------- Getters y setters (Firestore) ----------

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getClienteId() { return clienteId; }
    public void setClienteId(String clienteId) { this.clienteId = clienteId; }

    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }

    public String getHotelId() { return hotelId; }
    public void setHotelId(String hotelId) { this.hotelId = hotelId; }

    public String getHotelNombre() { return hotelNombre; }
    public void setHotelNombre(String hotelNombre) { this.hotelNombre = hotelNombre; }

    public String getHotelImagen() { return hotelImagen; }
    public void setHotelImagen(String hotelImagen) { this.hotelImagen = hotelImagen; }

    public String getHabitacionId() { return habitacionId; }
    public void setHabitacionId(String habitacionId) { this.habitacionId = habitacionId; }

    public String getHabitacionTipo() { return habitacionTipo; }
    public void setHabitacionTipo(String habitacionTipo) { this.habitacionTipo = habitacionTipo; }

    public String getFechaEntrada() { return fechaEntrada; }
    public void setFechaEntrada(String fechaEntrada) { this.fechaEntrada = fechaEntrada; }

    public String getFechaSalida() { return fechaSalida; }
    public void setFechaSalida(String fechaSalida) { this.fechaSalida = fechaSalida; }

    public int getNoches() { return noches; }
    public void setNoches(int noches) { this.noches = noches; }

    public double getPrecioNoche() { return precioNoche; }
    public void setPrecioNoche(double precioNoche) { this.precioNoche = precioNoche; }

    public double getMontoTotal() { return montoTotal; }
    public void setMontoTotal(double montoTotal) { this.montoTotal = montoTotal; }

    public String getTarjetaUltimos4() { return tarjetaUltimos4; }
    public void setTarjetaUltimos4(String tarjetaUltimos4) { this.tarjetaUltimos4 = tarjetaUltimos4; }

    public EstadoReserva getEstado() { return estado; }
    public void setEstado(EstadoReserva estado) { this.estado = estado; }

    public Date getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Date fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public EstadoCheckout getEstadoCheckout() { return estadoCheckout; }
    public void setEstadoCheckout(EstadoCheckout estadoCheckout) { this.estadoCheckout = estadoCheckout; }

    public double getValoracion() { return valoracion; }
    public void setValoracion(double valoracion) { this.valoracion = valoracion; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public Date getFechaCheckout() { return fechaCheckout; }
    public void setFechaCheckout(Date fechaCheckout) { this.fechaCheckout = fechaCheckout; }

    public boolean isTaxiSolicitado() { return taxiSolicitado; }
    public void setTaxiSolicitado(boolean taxiSolicitado) { this.taxiSolicitado = taxiSolicitado; }

    public String getServicioTaxiId() { return servicioTaxiId; }
    public void setServicioTaxiId(String servicioTaxiId) { this.servicioTaxiId = servicioTaxiId; }

    public double getCobroAdicionalMonto() { return cobroAdicionalMonto; }
    public void setCobroAdicionalMonto(double cobroAdicionalMonto) { this.cobroAdicionalMonto = cobroAdicionalMonto; }

    public String getCobroAdicionalMotivo() { return cobroAdicionalMotivo; }
    public void setCobroAdicionalMotivo(String cobroAdicionalMotivo) { this.cobroAdicionalMotivo = cobroAdicionalMotivo; }

    public String getCobroAdicionalObservacion() { return cobroAdicionalObservacion; }
    public void setCobroAdicionalObservacion(String cobroAdicionalObservacion) { this.cobroAdicionalObservacion = cobroAdicionalObservacion; }

    public double getMontoCobrado() { return montoCobrado; }
    public void setMontoCobrado(double montoCobrado) { this.montoCobrado = montoCobrado; }

    public Date getFechaCobro() { return fechaCobro; }
    public void setFechaCobro(Date fechaCobro) { this.fechaCobro = fechaCobro; }
}
