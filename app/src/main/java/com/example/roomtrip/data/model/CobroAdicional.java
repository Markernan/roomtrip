package com.example.roomtrip.data.model;

import java.util.EnumSet;

/**
 * Validación del cobro adicional por daños que el admin de hotel registra en el checkout.
 *
 * <p>RN-012 / RF-CHK-005: todo cobro adicional debe registrar obligatoriamente monto, motivo y
 * observación. Si no se llena ningún campo, simplemente no hay cobro adicional y el checkout se
 * puede procesar.
 */
public final class CobroAdicional {

    /** Qué falta o está mal en el cobro adicional. */
    public enum Error {
        MOTIVO_OBLIGATORIO,
        MONTO_OBLIGATORIO,
        MONTO_INVALIDO,
        OBSERVACION_OBLIGATORIA
    }

    private CobroAdicional() {}

    /** {@code true} si no se escribió nada: no hay cobro adicional que validar. */
    public static boolean estaVacio(String motivo, String monto, String observacion) {
        return esVacio(motivo) && esVacio(monto) && esVacio(observacion);
    }

    /**
     * Devuelve todos los errores a la vez (para señalar cada campo afectado). Un conjunto vacío
     * significa que el cobro es válido o que no hay cobro adicional.
     */
    public static EnumSet<Error> validar(String motivo, String monto, String observacion) {
        EnumSet<Error> errores = EnumSet.noneOf(Error.class);
        if (estaVacio(motivo, monto, observacion)) {
            return errores;
        }
        if (esVacio(motivo)) {
            errores.add(Error.MOTIVO_OBLIGATORIO);
        }
        if (esVacio(monto)) {
            errores.add(Error.MONTO_OBLIGATORIO);
        } else {
            Double valor = parsearMonto(monto);
            if (valor == null || valor <= 0) {
                errores.add(Error.MONTO_INVALIDO);
            }
        }
        if (esVacio(observacion)) {
            errores.add(Error.OBSERVACION_OBLIGATORIA);
        }
        return errores;
    }

    /** Convierte el texto del monto a número, o {@code null} si no es un número válido. */
    public static Double parsearMonto(String monto) {
        if (monto == null) return null;
        try {
            double valor = Double.parseDouble(monto.trim());
            return Double.isNaN(valor) || Double.isInfinite(valor) ? null : valor;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static boolean esVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }
}
