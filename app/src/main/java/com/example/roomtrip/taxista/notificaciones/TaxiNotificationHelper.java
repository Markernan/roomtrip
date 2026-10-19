package com.example.roomtrip.taxista.notificaciones;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.roomtrip.R;
import com.example.roomtrip.data.model.EstadoServicioTaxi;
import com.example.roomtrip.data.model.PedidoTaxi;
import com.example.roomtrip.data.model.ServicioTaxi;
import com.example.roomtrip.taxista.EnCaminoActivity;
import com.example.roomtrip.taxista.EnTrasladoActivity;
import com.example.roomtrip.taxista.SolicitadoActivity;

/**
 * Notificaciones del taxista.
 *  - Canal "taxi" (IMPORTANCE_HIGH): nuevos pedidos disponibles (heads-up).
 *  - Canal "taxi_estado" (IMPORTANCE_DEFAULT): notificación del servicio activo, que se
 *    ACTUALIZA reutilizando siempre el mismo ID en cada cambio de estado.
 */
public class TaxiNotificationHelper {

    public static final String CANAL_PEDIDOS = "taxi";
    public static final String CANAL_ESTADO = "taxi_estado";

    private static final int ID_NUEVO_PEDIDO = 1001;
    private static final int ID_SERVICIO_ACTIVO = 2001;

    // requestCodes distintos porque cada PendingIntent lleva un destino distinto
    private static final int RC_NUEVO_PEDIDO = 10;
    private static final int RC_SERVICIO_ACTIVO = 20;
    private static final int RC_SERVICIO_FINALIZADO = 30;

    private final Context context;
    private final NotificationManagerCompat manager;

    public TaxiNotificationHelper(Context context) {
        this.context = context.getApplicationContext();
        this.manager = NotificationManagerCompat.from(this.context);
        crearCanales();
    }

    /** Crear un canal que ya existe no hace nada, por eso es seguro llamarlo siempre. */
    public void crearCanales() {
        NotificationChannel canalPedidos = new NotificationChannel(CANAL_PEDIDOS,
                context.getString(R.string.notif_canal_pedidos), NotificationManager.IMPORTANCE_HIGH);
        canalPedidos.setDescription(context.getString(R.string.notif_canal_pedidos_descripcion));

        NotificationChannel canalEstado = new NotificationChannel(CANAL_ESTADO,
                context.getString(R.string.notif_canal_estado), NotificationManager.IMPORTANCE_DEFAULT);
        canalEstado.setDescription(context.getString(R.string.notif_canal_estado_descripcion));

        NotificationManager nm = context.getSystemService(NotificationManager.class);
        nm.createNotificationChannel(canalPedidos);
        nm.createNotificationChannel(canalEstado);
    }

    /** "Nuevo pedido disponible": al tocarla abre la lista de pedidos (SolicitadoActivity). */
    public void notificarNuevoPedido(PedidoTaxi pedido) {
        Intent intent = new Intent(context, SolicitadoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pi = PendingIntent.getActivity(context, RC_NUEVO_PEDIDO, intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CANAL_PEDIDOS)
                .setSmallIcon(R.drawable.ic_taxi)
                .setContentTitle(context.getString(R.string.notif_nuevo_pedido_titulo))
                .setContentText(context.getString(R.string.notif_ruta,
                        pedido.getOrigen(), pedido.getDestino()))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                        context.getString(R.string.notif_nuevo_pedido_detalle,
                                pedido.getNombrePasajero(), pedido.getOrigen(), pedido.getDestino())))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(pi)
                .setAutoCancel(true);

        notificar(ID_NUEVO_PEDIDO, builder);
    }

    /**
     * Muestra o actualiza la notificación persistente del servicio activo.
     * Al usar siempre ID_SERVICIO_ACTIVO, Android reemplaza la existente en vez de crear otra.
     */
    public void mostrarServicioActivo(ServicioTaxi servicio) {
        PedidoTaxi pedido = servicio.getPedido();
        Intent intent = new Intent(context, activityParaEstado(servicio.getEstado()));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pi = PendingIntent.getActivity(context, RC_SERVICIO_ACTIVO, intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CANAL_ESTADO)
                .setSmallIcon(R.drawable.ic_taxi)
                .setContentTitle(context.getString(R.string.notif_servicio_titulo,
                        servicio.getEstado().getEtiqueta()))
                .setContentText(context.getString(R.string.notif_servicio_texto,
                        pedido.getNombrePasajero(), pedido.getOrigen(), pedido.getDestino()))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_STATUS)
                .setOngoing(true)              // persistente: no se puede deslizar mientras dura el servicio
                .setOnlyAlertOnce(true)        // las actualizaciones no vuelven a sonar
                .setContentIntent(pi)
                .setAutoCancel(false);

        notificar(ID_SERVICIO_ACTIVO, builder);
    }

    /** Servicio FINALIZADO: se actualiza la misma notificación, deja de ser persistente y se cierra al tocarla. */
    public void mostrarServicioFinalizado(ServicioTaxi servicio) {
        PedidoTaxi pedido = servicio.getPedido();
        Intent intent = new Intent(context, SolicitadoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pi = PendingIntent.getActivity(context, RC_SERVICIO_FINALIZADO, intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CANAL_ESTADO)
                .setSmallIcon(R.drawable.ic_taxi)
                .setContentTitle(context.getString(R.string.notif_servicio_titulo,
                        EstadoServicioTaxi.FINALIZADO.getEtiqueta()))
                .setContentText(context.getString(R.string.notif_servicio_finalizado_texto,
                        pedido.getNombrePasajero(), pedido.getDestino()))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setOngoing(false)
                .setOnlyAlertOnce(true)
                .setContentIntent(pi)
                .setAutoCancel(true);

        notificar(ID_SERVICIO_ACTIVO, builder);
    }

    /** Usado al cerrar sesión. */
    public void cancelarTodas() {
        manager.cancel(ID_NUEVO_PEDIDO);
        manager.cancel(ID_SERVICIO_ACTIVO);
    }

    /** Pantalla del flujo que corresponde a cada estado del servicio. */
    public static Class<?> activityParaEstado(EstadoServicioTaxi estado) {
        switch (estado) {
            case ASIGNADO:
            case EN_CAMINO:
                return EnCaminoActivity.class;
            case EN_TRASLADO:
                return EnTrasladoActivity.class;
            default:
                return SolicitadoActivity.class;
        }
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
