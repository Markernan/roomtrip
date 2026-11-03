package com.example.roomtrip.data.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Hotel. Desde el Lab 6 también es el documento {@code hoteles/{id}} de Firestore; sus habitaciones y
 * servicios van en las subcolecciones {@code hoteles/{id}/habitaciones} y {@code .../servicios}.
 *
 * <p>{@code imagenResId} es un id de recurso de Android: cambia entre compilaciones, así que no se
 * guarda ({@code @Exclude}). En Firestore va {@code imagen}, una clave que {@code ImagenesHotel}
 * traduce a una foto incluida en la app (mientras se decide Storage o Cloudinary).
 */
public class Hotel implements Serializable {
    public static final String COLECCION = "hoteles";
    public static final String SUBCOLECCION_HABITACIONES = "habitaciones";
    public static final String SUBCOLECCION_SERVICIOS = "servicios";
    public static final String CAMPO_ESTADO = "estado";
    public static final String CAMPO_CALIFICACION = "calificacion";
    public static final String CAMPO_NUM_VALORACIONES = "numValoraciones";
    public static final String CAMPO_PRECIO = "precio";
    /** Monto mínimo por defecto para el taxi gratis al aeropuerto (RN-005), en soles. */
    public static final double MONTO_MINIMO_TAXI_POR_DEFECTO = 200.0;

    @DocumentId
    private String id;
    private String imagen;
    private String correo;
    private String descripcion;
    private double montoMinimoTaxi = MONTO_MINIMO_TAXI_POR_DEFECTO;
    private int numValoraciones;
    private List<String> lugaresCercanos = new ArrayList<>();
    // Solo para el reporte del Superadmin: se calculan, no se guardan
    private int reservasReporte;
    private double montoReporte;

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
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getMontoMinimoTaxi() { return montoMinimoTaxi; }
    public void setMontoMinimoTaxi(double montoMinimoTaxi) { this.montoMinimoTaxi = montoMinimoTaxi; }

    public int getNumValoraciones() { return numValoraciones; }
    public void setNumValoraciones(int numValoraciones) { this.numValoraciones = numValoraciones; }

    public List<String> getLugaresCercanos() { return lugaresCercanos; }
    public void setLugaresCercanos(List<String> lugaresCercanos) { this.lugaresCercanos = lugaresCercanos; }

    @Exclude public int getReservasReporte() { return reservasReporte; }
    @Exclude public void setReservasReporte(int reservasReporte) { this.reservasReporte = reservasReporte; }

    @Exclude public double getMontoReporte() { return montoReporte; }
    @Exclude public void setMontoReporte(double montoReporte) { this.montoReporte = montoReporte; }

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

    @Exclude public int getImagenResId() { return imagenResId; }
    @Exclude public void setImagenResId(int imagenResId) { this.imagenResId = imagenResId; }

    public String getAdminNombre() { return adminNombre; }
    public void setAdminNombre(String adminNombre) { this.adminNombre = adminNombre; }

    public String getAdminEmail() { return adminEmail; }
    public void setAdminEmail(String adminEmail) { this.adminEmail = adminEmail; }
}