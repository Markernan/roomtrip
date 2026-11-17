package com.example.roomtrip.data.api;

/**
 * Decide si un taxista puede entrar a la app a partir de lo que dice la API (el portal web es quien
 * lo habilita, RF-WTX-004). Es lógica pura, sin Android, para poder probarla con pruebas unitarias.
 */
public final class AccesoTaxista {

    public enum Resultado {
        /** Aprobado en el portal: puede entrar. */
        PERMITIDO,
        /** Registrado en el portal pero el Superadmin aún no lo aprueba. */
        PENDIENTE,
        /** Rechazado o inhabilitado por el Superadmin. */
        RECHAZADO,
        /** Su correo no figura en el portal de taxistas. */
        NO_REGISTRADO,
        /** La API no respondió: no se puede verificar. Las reservas del resto de roles siguen operando. */
        SERVICIO_NO_DISPONIBLE,
        /** La app no tiene configurada la API (solo builds de desarrollo sin local.properties). */
        SIN_CONFIGURAR,
        /** Otro fallo (credencial de la app inválida, permisos...). */
        ERROR
    }

    private AccesoTaxista() {
    }

    public static Resultado desdeTaxista(TaxistaApi taxista) {
        if (taxista == null) {
            return Resultado.NO_REGISTRADO;
        }
        if (taxista.estaAprobado()) {
            return Resultado.PERMITIDO;
        }
        if (TaxistaApi.HABILITACION_PENDIENTE.equals(taxista.getEstadoHabilitacion())) {
            return Resultado.PENDIENTE;
        }
        // RECHAZADO, INHABILITADO o cualquier valor que no conozcamos: nunca se deja pasar
        return Resultado.RECHAZADO;
    }

    public static Resultado desdeError(ApiException error) {
        switch (error.getTipo()) {
            case NO_ENCONTRADO:
                return Resultado.NO_REGISTRADO;
            case NO_DISPONIBLE:
            case DEMASIADOS_INTENTOS:
                return Resultado.SERVICIO_NO_DISPONIBLE;
            case NO_CONFIGURADA:
                return Resultado.SIN_CONFIGURAR;
            default:
                return Resultado.ERROR;
        }
    }
}
