package pe.pucp.tel05.taxi.calificacion.domain;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * RF-WTX-006. Una calificacion por servicio de taxi finalizado.
 *
 * <p>servicioTaxiId es unico: evita que el mismo traslado se califique dos
 * veces si la app movil reintenta la llamada.
 */
@Document(collection = "calificaciones")
public class Calificacion {

    @Id
    private String id;

    @Indexed
    private String taxistaId;

    @Indexed(unique = true)
    private String servicioTaxiId;

    private int puntuacion;

    private String observacion;

    private Instant fecha = Instant.now();

    public Calificacion() {
    }

    public Calificacion(String taxistaId, String servicioTaxiId, int puntuacion, String observacion) {
        this.taxistaId = taxistaId;
        this.servicioTaxiId = servicioTaxiId;
        this.puntuacion = puntuacion;
        this.observacion = observacion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaxistaId() {
        return taxistaId;
    }

    public void setTaxistaId(String taxistaId) {
        this.taxistaId = taxistaId;
    }

    public String getServicioTaxiId() {
        return servicioTaxiId;
    }

    public void setServicioTaxiId(String servicioTaxiId) {
        this.servicioTaxiId = servicioTaxiId;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public Instant getFecha() {
        return fecha;
    }

    public void setFecha(Instant fecha) {
        this.fecha = fecha;
    }
}
