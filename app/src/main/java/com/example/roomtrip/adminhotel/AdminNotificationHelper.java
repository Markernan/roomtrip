package com.example.roomtrip.adminhotel;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.roomtrip.MainActivity;
import com.example.roomtrip.R;

/**
 * Notificaciones para el Administrador de Hotel (Hito 5).
 * Rango de IDs: 4001 - 4999 para evitar colisiones.
 */
public class AdminNotificationHelper {
    private static final String TAG = "AdminNotifications";

    public static final String CANAL_URGENTE = "admin_urgente";
    public static final String CANAL_GENERAL = "admin_general";

    // IDs de notificación
    public static final int ID_CHECKOUT_PENDIENTE = 4001;
    public static final int ID_NUEVO_MENSAJE = 4002;
    public static final int ID_NUEVA_RESERVA = 4003;
    public static final int ID_REPORTE_GUARDADO = 4004;

    // Request Codes
    private static final int RC_MAIN = 400;

    private final Context context;
    private final NotificationManagerCompat manager;

    public AdminNotificationHelper(Context context) {
        this.context = context.getApplicationContext();
        this.manager = NotificationManagerCompat.from(this.context);
        crearCanales();
    }

    public void crearCanales() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel chUrgente = new NotificationChannel(CANAL_URGENTE,
                    "Alertas Urgentes Admin", NotificationManager.IMPORTANCE_HIGH);
            chUrgente.setDescription("Notificaciones de checkouts y mensajes urgentes.");

            NotificationChannel chGeneral = new NotificationChannel(CANAL_GENERAL,
                    "Notificaciones Generales Admin", NotificationManager.IMPORTANCE_DEFAULT);
            chGeneral.setDescription("Avisos generales sobre reservas y reportes.");

            NotificationManager nm = context.getSystemService(NotificationManager.class);
            if (nm != null) {
                nm.createNotificationChannel(chUrgente);
                nm.createNotificationChannel(chGeneral);
            }
        }
    }

    /** Checkout de un cliente -> "Checkout pendiente de cobro" */
    public void notificarCheckout(String huesped, String habitacion, String idCheckout) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("deep_link", "checkout_cobro");
        intent.putExtra("id_checkout", idCheckout);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        String texto = "El huésped " + huesped + " (Hab. " + habitacion + ") ha solicitado checkout.";
        publicar(ID_CHECKOUT_PENDIENTE, CANAL_URGENTE, "Checkout pendiente de cobro", texto, intent);
    }

    /** Mensaje de un cliente con reserva activa */
    public void notificarMensaje(String huesped, String reservaCode) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("deep_link", "chat_detalle");
        intent.putExtra("reserva_code", reservaCode);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        String texto = "Nuevo mensaje de " + huesped + " sobre la reserva " + reservaCode;
        publicar(ID_NUEVO_MENSAJE, CANAL_URGENTE, "Nuevo mensaje de cliente", texto, intent);
    }

    /** Nueva reserva recibida */
    public void notificarReserva(String huesped, String info) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("deep_link", "reserva_detalle");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        publicar(ID_NUEVA_RESERVA, CANAL_GENERAL, "Nueva reserva recibida", info, intent);
    }
    
    /** Reporte guardado (SAF) */
    public void notificarReporteGuardado(String nombreArchivo) {
        publicar(ID_REPORTE_GUARDADO, CANAL_GENERAL, "Reporte guardado", "El archivo " + nombreArchivo + " se guardó correctamente.", null);
    }

    @SuppressLint("MissingPermission")
    private void publicar(int id, String canal, String titulo, String texto, Intent destino) {
        if (!manager.areNotificationsEnabled()) {
            Log.e(TAG, "Sin permiso POST_NOTIFICATIONS: no se muestra la notificación");
            return;
        }

        NotificationCompat.Builder b = new NotificationCompat.Builder(context, canal)
                .setSmallIcon(R.drawable.ic_notification) // Usando ícono existente
                .setContentTitle(titulo)
                .setContentText(texto)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(texto))
                .setPriority(canal.equals(CANAL_URGENTE) ? NotificationCompat.PRIORITY_HIGH : NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        if (destino != null) {
            PendingIntent pi = PendingIntent.getActivity(context, id, destino,
                    PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            b.setContentIntent(pi);
        }

        try {
            manager.notify(id, b.build());
        } catch (SecurityException e) {
            Log.e(TAG, "Error de seguridad al notificar", e);
        }
    }
}
