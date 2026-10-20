package com.example.roomtrip.cliente.data;

/** Datos de la cuenta del cliente (RF-CL-01). La contraseña no se guarda: la gestionará Firebase Auth (RNF-01). */
public class PerfilCliente {

    public final String nombres;
    public final String apellidos;
    public final int tipoDocumento;
    public final String numeroDocumento;
    public final String fechaNacimiento;
    public final String correo;
    public final String telefono;
    public final String domicilio;

    /** tipoDocumento es la posición en el arreglo de recursos cliente_tipos_documento. */
    public PerfilCliente(String nombres, String apellidos, int tipoDocumento, String numeroDocumento,
                         String fechaNacimiento, String correo, String telefono, String domicilio) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.fechaNacimiento = fechaNacimiento;
        this.correo = correo;
        this.telefono = telefono;
        this.domicilio = domicilio;
    }
}
