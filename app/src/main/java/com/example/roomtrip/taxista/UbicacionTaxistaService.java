package com.example.roomtrip.taxista;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.roomtrip.R;
import com.example.roomtrip.data.firebase.ServicioTaxiRepository;
import com.google.firebase.firestore.ListenerRegistration;

/**
 * Servicio en primer plano (tipo {@code location}) que sigue reportando la ubicación del taxista aunque
 * la pantalla se apague o la app pase a segundo plano durante un servicio activo (RNF-05).
 *
 * <p>Lo arranca {@link MapaTaxista} desde una pantalla visible (Android 14 no permite iniciar un
 * servicio de ubicación desde segundo plano). Se detiene solo cuando el servicio de taxi deja de estar
 * en curso (finalizado, cancelado o borrado), y muestra una notificación fija mientras reporta.
 */
public class UbicacionTaxistaService extends Service {

    private static final String TAG = "UbicacionService";
    private static final String EXTRA_SERVICIO_ID = "servicioId";
    private static final String CANAL_ID = "ubicacion_servicio";
    private static final int NOTIFICACION_ID = 4201;

    @Nullable
    private String servicioId;
    @Nullable
    private RastreoUbicacionTaxista rastreo;
    @Nullable
    private ListenerRegistration vigilante;

    /** Empieza a reportar la ubicación del servicio; no hace nada sin permiso de ubicación. */
    public static void iniciar(Context context, String servicioId) {
        if (servicioId == null || !RastreoUbicacionTaxista.tienePermiso(context)) return;
        Intent intent = new Intent(context, UbicacionTaxistaService.class)
                .putExtra(EXTRA_SERVICIO_ID, servicioId);
        ContextCompat.startForegroundService(context, intent);
    }

    public static void detener(Context context) {
        context.stopService(new Intent(context, UbicacionTaxistaService.class));
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        String id = intent != null ? intent.getStringExtra(EXTRA_SERVICIO_ID) : null;
        if (id == null) {
            stopSelf();
            return START_NOT_STICKY;
        }
        if (id.equals(servicioId)) return START_NOT_STICKY; // ya estaba reportando este servicio

        startForeground(NOTIFICACION_ID, crearNotificacion(),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION);
        liberar();
        servicioId = id;

        ServicioTaxiRepository repositorio = new ServicioTaxiRepository();
        rastreo = new RastreoUbicacionTaxista(this, repositorio, null);
        rastreo.iniciar(id);
        // Termina solo cuando el servicio ya no está en curso
        vigilante = repositorio.escucharServicio(id, servicio -> {
            if (servicio == null || !servicio.estaEnCurso()) stopSelf();
        }, e -> Log.w(TAG, "No se pudo vigilar el servicio " + id, e));
        return START_NOT_STICKY;
    }

    private Notification crearNotificacion() {
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(new NotificationChannel(CANAL_ID,
                getString(R.string.ubicacion_canal), NotificationManager.IMPORTANCE_LOW));

        Intent abrir = getPackageManager().getLaunchIntentForPackage(getPackageName());
        PendingIntent contenido = abrir == null ? null : PendingIntent.getActivity(this, 0, abrir,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        return new NotificationCompat.Builder(this, CANAL_ID)
                .setSmallIcon(R.drawable.ic_location)
                .setContentTitle(getString(R.string.ubicacion_titulo))
                .setContentText(getString(R.string.ubicacion_texto))
                .setOngoing(true)
                .setContentIntent(contenido)
                .build();
    }

    private void liberar() {
        if (rastreo != null) rastreo.detener();
        if (vigilante != null) vigilante.remove();
        rastreo = null;
        vigilante = null;
    }

    @Override
    public void onDestroy() {
        liberar();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
