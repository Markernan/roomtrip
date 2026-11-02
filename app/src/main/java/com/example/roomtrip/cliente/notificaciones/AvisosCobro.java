package com.example.roomtrip.cliente.notificaciones;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.roomtrip.cliente.data.SesionClienteManager;
import com.example.roomtrip.data.firebase.ReservaRepository;
import com.example.roomtrip.data.model.EstadoCheckout;
import com.example.roomtrip.data.model.Reserva;
import com.google.firebase.firestore.ListenerRegistration;

/**
 * RF-CL-05: el cliente recibe una notificación cuando el hotel le cobra. Se escuchan sus reservas en
 * Firestore mientras la app está abierta; cuando una pasa a checkout PROCESADO se avisa una sola vez
 * (el id queda guardado en SharedPreferences). Con la app cerrada haría falta Cloud Messaging.
 */
public final class AvisosCobro {

    private AvisosCobro() {
    }

    public static ListenerRegistration escuchar(@NonNull Context context, @NonNull String uidCliente) {
        Context app = context.getApplicationContext();
        SesionClienteManager sesion = new SesionClienteManager(app);
        ClienteNotificationHelper notificaciones = new ClienteNotificationHelper(app);
        return new ReservaRepository().escucharDeCliente(uidCliente, reservas -> {
            for (Reserva r : reservas) {
                if (r.getEstadoCheckout() == EstadoCheckout.PROCESADO && r.getId() != null
                        && !sesion.cobroYaNotificado(r.getId())) {
                    notificaciones.notificarCobro(r.getHotelNombre(), r.getMontoCobrado());
                    sesion.marcarCobroNotificado(r.getId());
                }
            }
        }, e -> Log.w("AvisosCobro", "No se pudieron escuchar las reservas del cliente", e));
    }
}
