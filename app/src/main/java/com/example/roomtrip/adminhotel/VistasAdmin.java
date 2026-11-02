package com.example.roomtrip.adminhotel;

import android.content.Context;

import com.example.roomtrip.R;
import com.example.roomtrip.data.model.Chat;
import com.example.roomtrip.data.model.Checkout;
import com.example.roomtrip.data.model.EstadoCheckout;
import com.example.roomtrip.data.model.Notificacion;
import com.example.roomtrip.data.model.Reserva;
import com.example.roomtrip.data.model.ServicioTaxi;
import com.example.roomtrip.data.model.Traslado;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Convierte reservas y servicios de taxi de Firestore a los modelos de vista que ya usan los adapters
 * del administrador de hotel (Checkout, Chat, Notificacion, Traslado). Así esas listas no cambian.
 */
public final class VistasAdmin {

    private VistasAdmin() {
    }

    private static String hora(Date fecha) {
        return fecha != null ? new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(fecha) : "";
    }

    /** Checkout solicitado por el cliente; el monto es el cobrado o, si aún no se cobra, el de la reserva. */
    public static Checkout checkout(Reserva r) {
        EstadoCheckout estado = r.getEstadoCheckout() != null ? r.getEstadoCheckout() : EstadoCheckout.PENDIENTE;
        double monto = r.getMontoCobrado() > 0 ? r.getMontoCobrado() : r.getMontoTotal();
        Checkout c = new Checkout(r.getClienteNombre(), r.getHabitacionTipo(),
                r.getFechaCheckout() != null ? hora(r.getFechaCheckout()) : r.getFechaSalida(), estado, monto);
        c.setReservaId(r.getId());
        return c;
    }

    /** Un chat por reserva activa (RN-006). */
    public static Chat chat(Context context, Reserva r) {
        Chat c = new Chat(r.getClienteNombre(), r.getHabitacionTipo(),
                context.getString(R.string.chats_abrir_conversacion, r.getFechaEntrada(), r.getFechaSalida()), "", 0);
        c.setReservaId(r.getId());
        return c;
    }

    /** Aviso para el hotel: checkout pendiente de cobro o reserva nueva. */
    public static Notificacion notificacion(Context context, Reserva r) {
        if (r.getEstadoCheckout() == EstadoCheckout.PENDIENTE) {
            return new Notificacion(context.getString(R.string.notif_admin_checkout_titulo),
                    context.getString(R.string.notif_admin_checkout_texto, r.getClienteNombre(), r.getHabitacionTipo()),
                    hora(r.getFechaCheckout()), Notificacion.TipoNotificacion.PAGO, false);
        }
        return new Notificacion(context.getString(R.string.notif_admin_reserva_titulo),
                context.getString(R.string.notif_admin_reserva_texto, r.getClienteNombre(), r.getHabitacionTipo(),
                        r.getFechaEntrada(), r.getFechaSalida()),
                hora(r.getFechaCreacion()), Notificacion.TipoNotificacion.RESERVA, r.getEstadoCheckout() != null);
    }

    /** Traslado gratuito hotel → aeropuerto (RF del admin: ver el estado del taxista). */
    public static Traslado traslado(Context context, ServicioTaxi s) {
        String conductor = s.getTaxistaNombre() != null ? s.getTaxistaNombre()
                : context.getString(R.string.traslados_asignando_conductor);
        return new Traslado(s.getClienteNombre(), "—",
                context.getString(R.string.notif_ruta, s.getHotelNombre(), s.getAeropuerto()),
                hora(s.getFechaSolicitud()), s.getEstado(), conductor, 0);
    }
}
