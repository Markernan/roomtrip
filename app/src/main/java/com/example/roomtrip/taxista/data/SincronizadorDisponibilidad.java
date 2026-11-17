package com.example.roomtrip.taxista.data;

import android.content.Context;
import android.util.Log;

import com.example.roomtrip.data.api.ApiException;
import com.example.roomtrip.data.api.TaxiServicios;
import com.example.roomtrip.data.model.RolUsuario;
import com.example.roomtrip.utils.RegistroEventos;

/**
 * Refleja en la API de taxistas la disponibilidad del taxista (RF-WTX-007): EN_SERVICIO al aceptar un
 * traslado, DISPONIBLE al terminarlo, y DISPONIBLE / NO_DISPONIBLE con el interruptor de la pantalla.
 *
 * <p>Es "mejor esfuerzo": el pedido en tiempo real vive en Firestore y no debe detenerse porque la API
 * esté dormida o caída (RF-TAX-018). Si falla, se registra y la app sigue.
 */
public final class SincronizadorDisponibilidad {

    private static final String TAG = "SincronizadorDisp";

    private SincronizadorDisponibilidad() {
    }

    /** @param estado una de las constantes {@code TaxistaApi.DISPONIBLE / EN_SERVICIO / NO_DISPONIBLE} */
    public static void enviar(Context contexto, String estado) {
        String idApi = new SesionTaxistaManager(contexto).getIdTaxistaApi();
        if (idApi.isEmpty()) {
            return; // build sin API configurada: no hay con qué sincronizar
        }
        TaxiServicios.repositorio().cambiarDisponibilidad(idApi, estado,
                taxista -> Log.d(TAG, "Disponibilidad en la API: " + estado),
                e -> {
                    Log.w(TAG, "No se pudo actualizar la disponibilidad a " + estado, e);
                    if (e instanceof ApiException && ((ApiException) e).esNoDisponible()) {
                        RegistroEventos.registrar(RegistroEventos.API_TAXISTAS_NO_DISPONIBLE, RolUsuario.TAXISTA,
                                "No se pudo sincronizar la disponibilidad (" + estado + ")");
                    }
                });
    }
}
