package com.example.roomtrip.superadmin;

import com.example.roomtrip.data.model.EstadoCuenta;
import com.example.roomtrip.data.model.PerfilUsuario;
import com.example.roomtrip.data.model.RolUsuario;
import com.example.roomtrip.data.model.Usuario;
import com.example.roomtrip.utils.Constants;

/**
 * Convierte el documento {@code usuarios/{uid}} de Firestore al modelo {@link Usuario} que ya usan
 * UsuariosAdapter y DetalleUsuarioActivity. Así las pantallas del Superadmin no cambian de forma.
 */
public final class UsuarioVista {

    private UsuarioVista() {
    }

    public static Usuario desde(PerfilUsuario p) {
        Usuario u = new Usuario(p.nombreCompleto(), p.getCorreo(), etiquetaRol(p.getRol()), p.getTelefono(),
                p.getEstado() == EstadoCuenta.ACTIVO);
        u.setUid(p.getUid());
        u.setPlacaAuto(p.getPlaca());
        u.setHotelAsignado(p.getHotelNombre());
        u.setHotelId(p.getHotelId());
        u.setDireccion(p.getDomicilio());
        if (p.getEstadoAprobacion() != null) {
            u.setEstadoAprobacion(p.getEstadoAprobacion().name());
        }
        return u;
    }

    /** Los filtros de UsuariosActivity comparan contra estos textos ("Cliente", "Admin", "Taxista"). */
    public static String etiquetaRol(RolUsuario rol) {
        if (rol == null) return "";
        switch (rol) {
            case CLIENTE:
                return Constants.ROL_CLIENTE;
            case ADMIN_HOTEL:
                return Constants.ROL_ADMIN;
            case TAXISTA:
                return Constants.ROL_TAXISTA;
            case SUPERADMIN:
            default:
                return Constants.ROL_SUPERADMIN;
        }
    }
}
