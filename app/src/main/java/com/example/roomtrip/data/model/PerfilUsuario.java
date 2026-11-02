package com.example.roomtrip.data.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Documento {@code usuarios/{uid}} de Firestore, igual para los cuatro roles. El id del documento es
 * el uid de Firebase Authentication. Tras el login se lee el {@code rol} y se navega a su pantalla.
 *
 * <p>POJO para {@code toObject()} y {@code set()} (Clase 9.2): constructor vacío y getters/setters.
 * Los enums se guardan por su nombre. La contraseña nunca se guarda aquí: la gestiona Firebase
 * Authentication. Los métodos de reglas de negocio no empiezan con {@code get}/{@code is}, para que
 * Firestore no los guarde como campos.
 */
public class PerfilUsuario {

    public static final String COLECCION = "usuarios";

    // Campos que el propio usuario puede editar (las reglas impiden tocar el rol, el estado, etc.)
    public static final String CAMPO_NOMBRES = "nombres";
    public static final String CAMPO_APELLIDOS = "apellidos";
    public static final String CAMPO_TIPO_DOCUMENTO = "tipoDocumento";
    public static final String CAMPO_NUMERO_DOCUMENTO = "numeroDocumento";
    public static final String CAMPO_FECHA_NACIMIENTO = "fechaNacimiento";
    public static final String CAMPO_TELEFONO = "telefono";
    public static final String CAMPO_DOMICILIO = "domicilio";
    public static final String CAMPO_PLACA = "placa";
    // Campos que solo cambia el Superadmin
    public static final String CAMPO_ROL = "rol";
    public static final String CAMPO_ESTADO = "estado";
    public static final String CAMPO_ESTADO_APROBACION = "estadoAprobacion";
    public static final String CAMPO_HOTEL_ID = "hotelId";

    @DocumentId
    private String uid;

    private String nombres;
    private String apellidos;
    private String tipoDocumento;      // "DNI", "Pasaporte" o "Carné de extranjería"
    private String numeroDocumento;
    private String fechaNacimiento;
    private String correo;
    private String telefono;
    private String domicilio;

    private String placa;              // solo taxista
    private String hotelId;            // solo administrador de hotel
    private String hotelNombre;        // solo administrador de hotel

    private RolUsuario rol;
    private EstadoCuenta estado;
    private EstadoAprobacion estadoAprobacion; // solo taxista

    @ServerTimestamp
    private Date fechaRegistro;

    /** Requerido por Firestore para {@code toObject()}. */
    public PerfilUsuario() {
    }

    private static PerfilUsuario base(RolUsuario rol, String nombres, String correo) {
        PerfilUsuario p = new PerfilUsuario();
        p.rol = rol;
        p.nombres = nombres != null ? nombres : "";
        p.apellidos = "";
        p.correo = correo;
        p.estado = EstadoCuenta.ACTIVO;
        return p;
    }

    /** RF del cliente: se autoregistra y queda habilitado automáticamente. */
    public static PerfilUsuario nuevoCliente(String nombres, String apellidos, String tipoDocumento,
                                             String numeroDocumento, String fechaNacimiento, String correo,
                                             String telefono, String domicilio) {
        PerfilUsuario p = base(RolUsuario.CLIENTE, nombres, correo);
        p.apellidos = apellidos;
        p.tipoDocumento = tipoDocumento;
        p.numeroDocumento = numeroDocumento;
        p.fechaNacimiento = fechaNacimiento;
        p.telefono = telefono;
        p.domicilio = domicilio;
        return p;
    }

    /** Taxista: queda PENDIENTE hasta que el Superadmin lo apruebe. */
    public static PerfilUsuario nuevaSolicitudTaxista(String nombres, String correo) {
        PerfilUsuario p = base(RolUsuario.TAXISTA, nombres, correo);
        p.estadoAprobacion = EstadoAprobacion.PENDIENTE;
        return p;
    }

    /** Administrador de hotel registrado por el Superadmin (invitación por correo). */
    public static PerfilUsuario nuevoAdminHotel(String nombres, String correo, String hotelId, String hotelNombre) {
        PerfilUsuario p = base(RolUsuario.ADMIN_HOTEL, nombres, correo);
        p.hotelId = hotelId;
        p.hotelNombre = hotelNombre;
        return p;
    }

    // ---------- Reglas de negocio (no son getters: no se guardan) ----------

    /** Cuenta activa y, si es taxista, aprobada por el Superadmin. El admin además necesita hotel. */
    public boolean puedeOperar() {
        if (rol == null || estado != EstadoCuenta.ACTIVO) return false;
        switch (rol) {
            case TAXISTA:
                return estadoAprobacion == EstadoAprobacion.APROBADO;
            case ADMIN_HOTEL:
                return hotelId != null && !hotelId.isEmpty();
            default:
                return true;
        }
    }

    /** "Nombres Apellidos", sin espacios de sobra si falta alguno. */
    public String nombreCompleto() {
        String n = nombres != null ? nombres.trim() : "";
        String a = apellidos != null ? apellidos.trim() : "";
        return (n + " " + a).trim();
    }

    /** Primer nombre para el saludo ("Hola, Ana"). */
    public String primerNombre() {
        String n = nombres != null ? nombres.trim() : "";
        int espacio = n.indexOf(' ');
        return espacio > 0 ? n.substring(0, espacio) : n;
    }

    // ---------- Getters y setters (Firestore) ----------

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public String getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(String fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDomicilio() { return domicilio; }
    public void setDomicilio(String domicilio) { this.domicilio = domicilio; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public String getHotelId() { return hotelId; }
    public void setHotelId(String hotelId) { this.hotelId = hotelId; }

    public String getHotelNombre() { return hotelNombre; }
    public void setHotelNombre(String hotelNombre) { this.hotelNombre = hotelNombre; }

    public RolUsuario getRol() { return rol; }
    public void setRol(RolUsuario rol) { this.rol = rol; }

    public EstadoCuenta getEstado() { return estado; }
    public void setEstado(EstadoCuenta estado) { this.estado = estado; }

    public EstadoAprobacion getEstadoAprobacion() { return estadoAprobacion; }
    public void setEstadoAprobacion(EstadoAprobacion estadoAprobacion) { this.estadoAprobacion = estadoAprobacion; }

    public Date getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Date fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}
