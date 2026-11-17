package com.example.roomtrip.data.api;

/**
 * Vista del taxista que entrega la API a la app. Es una vista <b>reducida</b>: no trae número de
 * documento, fecha de nacimiento ni domicilio, porque la app no los necesita.
 *
 * <p>Los nombres de los campos son los del JSON de la API y no se renombran (Gson los usa tal cual).
 * Los estados llegan como texto con los mismos valores que documenta {@code docs/API.md}.
 */
public class TaxistaApi {

    public static final String HABILITACION_PENDIENTE = "PENDIENTE";
    public static final String HABILITACION_APROBADO = "APROBADO";
    public static final String HABILITACION_RECHAZADO = "RECHAZADO";
    public static final String HABILITACION_INHABILITADO = "INHABILITADO";

    public static final String DISPONIBLE = "DISPONIBLE";
    public static final String EN_SERVICIO = "EN_SERVICIO";
    public static final String NO_DISPONIBLE = "NO_DISPONIBLE";

    private String id;
    private String nombres;
    private String apellidos;
    private String correo;
    private String telefono;
    private String fotoUrl;
    private String vehiculoMarca;
    private String vehiculoModelo;
    private String vehiculoAnio;
    private String vehiculoColor;
    private String placa;
    private String fotoVehiculoUrl;
    private String estadoHabilitacion;
    private String estadoDisponibilidad;
    private double valoracionPromedio;
    private long totalCalificaciones;

    public TaxistaApi() {
    }

    public String getId() {
        return id;
    }

    public String getNombres() {
        return nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getCorreo() {
        return correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public String getVehiculoMarca() {
        return vehiculoMarca;
    }

    public String getVehiculoModelo() {
        return vehiculoModelo;
    }

    public String getVehiculoAnio() {
        return vehiculoAnio;
    }

    public String getVehiculoColor() {
        return vehiculoColor;
    }

    public String getPlaca() {
        return placa;
    }

    public String getFotoVehiculoUrl() {
        return fotoVehiculoUrl;
    }

    public String getEstadoHabilitacion() {
        return estadoHabilitacion;
    }

    public String getEstadoDisponibilidad() {
        return estadoDisponibilidad;
    }

    public double getValoracionPromedio() {
        return valoracionPromedio;
    }

    public long getTotalCalificaciones() {
        return totalCalificaciones;
    }

    /** "Nombres Apellidos", tolerando que alguno venga vacío. */
    public String nombreCompleto() {
        String n = nombres == null ? "" : nombres.trim();
        String a = apellidos == null ? "" : apellidos.trim();
        return (n + " " + a).trim();
    }

    /** "Toyota Yaris 2020 · Blanco", sin los pedazos que falten. */
    public String descripcionVehiculo() {
        StringBuilder sb = new StringBuilder();
        for (String parte : new String[] {vehiculoMarca, vehiculoModelo, vehiculoAnio}) {
            if (parte != null && !parte.trim().isEmpty()) {
                if (sb.length() > 0) {
                    sb.append(' ');
                }
                sb.append(parte.trim());
            }
        }
        if (vehiculoColor != null && !vehiculoColor.trim().isEmpty()) {
            sb.append(sb.length() > 0 ? " · " : "").append(vehiculoColor.trim());
        }
        return sb.toString();
    }

    /** "★ 4.7 (12)", o un texto neutro si todavía no tiene calificaciones (RF-CL-09). */
    public String descripcionValoracion() {
        if (totalCalificaciones <= 0) {
            return "Sin calificaciones todavía";
        }
        return String.format(java.util.Locale.US, "★ %.1f (%d)", valoracionPromedio, totalCalificaciones);
    }

    /** Solo un taxista aprobado puede prestar servicios (RF-WTX-003). */
    public boolean estaAprobado() {
        return HABILITACION_APROBADO.equals(estadoHabilitacion);
    }

    public boolean estaDisponible() {
        return estaAprobado() && DISPONIBLE.equals(estadoDisponibilidad);
    }
}
