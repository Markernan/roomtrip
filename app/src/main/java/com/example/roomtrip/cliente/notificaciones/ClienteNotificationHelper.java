package com.example.roomtrip.cliente.notificaciones;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.roomtrip.R;
import com.example.roomtrip.cliente.MisReservasActivity;

import java.util.Locale;

/**
 * Notificaciones del cliente (RF-CL-05).
 *  - Canal "cliente_cobros" (IMPORTANCE_HIGH): aviso con el monto cobrado al finalizar el checkout.
 */
public class ClienteNotificationHelper {

    public static final String CANAL_COBROS = "cliente_cobros";

    private static final int ID_COBRO = 3001;
    private static final int RC_COBRO = 40;

    private final Context context;
    private final NotificationManagerCompat manager;

    public ClienteNotificationHelper(Context context) {
        this.context = context.getApplicationContext();
        this.manager = NotificationManagerCompat.from(this.context);
        crearCanales();
    }

    /** Crear un canal que ya existe no hace nada, por eso es seguro llamarlo siempre. */
    public void crearCanales() {
        NotificationChannel canalCobros = new NotificationChannel(CANAL_COBROS,
                context.getString(R.string.notif_canal_cobros), NotificationManager.IMPORTANCE_HIGH);
        canalCobros.setDescription(context.getString(R.string.notif_canal_cobros_descripcion));

        context.getSystemService(NotificationManager.class).createNotificationChannel(canalCobros);
    }

    /** "Cobro realizado": al tocarla abre Mis reservas. */
    public void notificarCobro(String hotel, double monto) {
        Intent intent = new Intent(context, MisReservasActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pi = PendingIntent.getActivity(context, RC_COBRO, intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        String texto = context.getString(R.string.notif_cobro_texto, formatearMonto(monto), hotel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CANAL_COBROS)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(context.getString(R.string.notif_cobro_titulo))
                .setContentText(texto)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(texto))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_STATUS)
                .setContentIntent(pi)
                .setAutoCancel(true);

        notificar(ID_COBRO, builder);
    }

    /** Usado al cerrar sesión. */
    public void cancelarTodas() {
        manager.cancel(ID_COBRO);
    }

    public static String formatearMonto(double monto) {
        return String.format(Locale.US, "US$%.2f", monto);
    }

    // areNotificationsEnabled() devuelve false si el usuario no concedió POST_NOTIFICATIONS
    // o si desactivó las notificaciones de la app, así evitamos un SecurityException.
    @SuppressLint("MissingPermission")
    private void notificar(int id, NotificationCompat.Builder builder) {
        if (manager.areNotificationsEnabled()) {
            manager.notify(id, builder.build());
        }
    }
}
