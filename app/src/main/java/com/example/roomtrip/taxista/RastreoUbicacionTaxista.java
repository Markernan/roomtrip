package com.example.roomtrip.taxista;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.roomtrip.data.firebase.ServicioTaxiRepository;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.model.LatLng;

/**
 * Registra automáticamente la ubicación del taxista durante un servicio activo (Lab 7, requisito de
 * calidad: "la ubicación del taxista deberá actualizarse periódicamente").
 *
 * <p>Pide posiciones al Fused Location Provider cada {@link #INTERVALO_MS} y las guarda en el
 * documento del servicio en Firestore ({@code ubicacionTaxista}), de donde las lee el cliente para
 * mover el taxi en su mapa. Lo usan las pantallas del servicio en curso ({@link EnCaminoActivity} y
 * {@link EnTrasladoActivity}): {@link #iniciar} en {@code onStart} y {@link #detener} en
 * {@code onStop}.
 */
public class RastreoUbicacionTaxista {

    private static final String TAG = "RastreoUbicacion";

    /** Cada cuánto se pide y se guarda la posición. */
    public static final long INTERVALO_MS = 10_000;
    /** Intervalo mínimo si el sistema entrega posiciones antes de tiempo. */
    private static final long INTERVALO_MINIMO_MS = 5_000;

    /** Avisa a la pantalla de cada nueva posición, para dibujarla en su propio mapa. */
    public interface OyenteUbicacion {
        void onUbicacion(@NonNull LatLng posicion);
    }

    private final Context context;
    private final FusedLocationProviderClient proveedor;
    private final ServicioTaxiRepository repositorio;
    @Nullable
    private final OyenteUbicacion oyente;

    @Nullable
    private String servicioId;
    private boolean activo;
    private long ultimoEnvioMs;

    private final LocationCallback callback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult resultado) {
            Location ultima = resultado.getLastLocation();
            if (ultima != null) procesar(ultima);
        }
    };

    public RastreoUbicacionTaxista(@NonNull Context context, @NonNull ServicioTaxiRepository repositorio,
                                   @Nullable OyenteUbicacion oyente) {
        this.context = context.getApplicationContext();
        this.proveedor = LocationServices.getFusedLocationProviderClient(context);
        this.repositorio = repositorio;
        this.oyente = oyente;
    }

    public static boolean tienePermiso(@NonNull Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Empieza (o sigue) reportando la ubicación del servicio indicado. Si ya estaba activo con el
     * mismo servicio no hace nada; sin permiso de ubicación tampoco (la pantalla lo pide antes).
     */
    @SuppressWarnings("MissingPermission") // se comprueba con tienePermiso()
    public void iniciar(@NonNull String servicioId) {
        if (activo && servicioId.equals(this.servicioId)) return;
        if (!tienePermiso(context)) {
            Log.w(TAG, "Sin permiso de ubicación: no se reporta la posición del taxista");
            return;
        }
        this.servicioId = servicioId;
        this.ultimoEnvioMs = 0;

        LocationRequest pedido = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, INTERVALO_MS)
                .setMinUpdateIntervalMillis(INTERVALO_MINIMO_MS)
                .build();
        proveedor.requestLocationUpdates(pedido, callback, Looper.getMainLooper());
        activo = true;

        // Primera posición sin esperar al primer intervalo
        proveedor.getLastLocation().addOnSuccessListener(ubicacion -> {
            if (ubicacion != null && activo) procesar(ubicacion);
        });
    }

    public void detener() {
        if (!activo) return;
        proveedor.removeLocationUpdates(callback);
        activo = false;
        servicioId = null;
    }

    private void procesar(@NonNull Location ubicacion) {
        LatLng posicion = new LatLng(ubicacion.getLatitude(), ubicacion.getLongitude());
        if (oyente != null) oyente.onUbicacion(posicion);

        // Firestore se escribe como mucho una vez por intervalo, aunque lleguen posiciones más seguido
        long ahora = System.currentTimeMillis();
        if (servicioId == null || ahora - ultimoEnvioMs < INTERVALO_MINIMO_MS) return;
        ultimoEnvioMs = ahora;
        String id = servicioId;
        repositorio.actualizarUbicacion(id, posicion.latitude, posicion.longitude,
                e -> Log.e(TAG, "No se pudo guardar la ubicación del servicio " + id, e));
    }
}
