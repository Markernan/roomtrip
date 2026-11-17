package com.example.roomtrip.utils;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Utilidades de Google Maps compartidas por las pantallas del taxi (Lab 7).
 *
 * <ul>
 *   <li>{@link #buscarLugar}: convierte un texto ("Aeropuerto Jorge Chávez", "Miraflores, Lima") en
 *       coordenadas con el {@link Geocoder} de Android. No necesita clave propia. Los resultados se
 *       guardan en memoria para no repetir la búsqueda en cada cambio del servicio.</li>
 *   <li>{@link #ponerMarcador} y {@link #encuadrar}: crean o mueven un marcador y ajustan la cámara
 *       para que se vean todos los puntos.</li>
 * </ul>
 */
public final class MapaHelper {

    private static final String TAG = "MapaHelper";

    /** Centro de Lima: posición inicial de la cámara mientras no hay ningún punto. */
    public static final LatLng LIMA = new LatLng(-12.0464, -77.0428);
    public static final float ZOOM_CIUDAD = 11f;
    public static final float ZOOM_CALLE = 15f;
    private static final int MARGEN_ENCUADRE_PX = 160;

    /** Sesgo de búsqueda: los hoteles y aeropuertos del proyecto están en Perú. */
    private static final String SUFIJO_PAIS = ", Perú";

    private static final Map<String, LatLng> CACHE = new HashMap<>();

    public interface ResultadoLugar {
        /** @param punto coordenadas encontradas, o null si el texto no se pudo ubicar */
        void onLugar(@Nullable LatLng punto);
    }

    private MapaHelper() {
    }

    /**
     * Busca las coordenadas de un lugar por su nombre o dirección. El resultado llega en el hilo
     * principal; si la actividad ya se cerró, no se llama.
     */
    public static void buscarLugar(@NonNull Activity activity, @Nullable String texto,
                                   @NonNull ResultadoLugar resultado) {
        if (texto == null || texto.trim().isEmpty() || !Geocoder.isPresent()) {
            resultado.onLugar(null);
            return;
        }
        String consulta = texto.trim() + SUFIJO_PAIS;
        synchronized (CACHE) {
            if (CACHE.containsKey(consulta)) {
                resultado.onLugar(CACHE.get(consulta));
                return;
            }
        }
        Geocoder geocoder = new Geocoder(activity, Locale.forLanguageTag("es-PE"));
        geocoder.getFromLocationName(consulta, 1, new Geocoder.GeocodeListener() {
            @Override
            public void onGeocode(@NonNull List<Address> direcciones) {
                LatLng punto = direcciones.isEmpty() ? null
                        : new LatLng(direcciones.get(0).getLatitude(), direcciones.get(0).getLongitude());
                entregar(activity, consulta, punto, resultado);
            }

            @Override
            public void onError(@Nullable String mensaje) {
                Log.w(TAG, "No se pudo ubicar \"" + consulta + "\": " + mensaje);
                entregar(activity, consulta, null, resultado);
            }
        });
    }

    private static void entregar(Activity activity, String consulta, @Nullable LatLng punto,
                                 ResultadoLugar resultado) {
        if (punto != null) {
            synchronized (CACHE) {
                CACHE.put(consulta, punto);
            }
        }
        activity.runOnUiThread(() -> {
            if (!activity.isFinishing() && !activity.isDestroyed()) resultado.onLugar(punto);
        });
    }

    /**
     * Crea el marcador si no existe o lo mueve si ya estaba. Con {@code punto} null lo quita.
     * Devuelve el marcador que hay que guardar (o null).
     *
     * @param tono uno de los {@code BitmapDescriptorFactory.HUE_*}
     */
    @Nullable
    public static Marker ponerMarcador(@NonNull GoogleMap mapa, @Nullable Marker actual, @Nullable LatLng punto,
                                       @NonNull String titulo, float tono) {
        if (punto == null) {
            if (actual != null) actual.remove();
            return null;
        }
        if (actual == null) {
            return mapa.addMarker(new MarkerOptions()
                    .position(punto)
                    .title(titulo)
                    .icon(BitmapDescriptorFactory.defaultMarker(tono)));
        }
        actual.setPosition(punto);
        actual.setTitle(titulo);
        return actual;
    }

    /** Menos de ~200 m entre las esquinas: para el encuadre cuentan como un solo punto. */
    private static boolean casiIguales(@NonNull LatLngBounds limites) {
        float[] metros = new float[1];
        android.location.Location.distanceBetween(limites.southwest.latitude, limites.southwest.longitude,
                limites.northeast.latitude, limites.northeast.longitude, metros);
        return metros[0] < 200f;
    }

    /** Ajusta la cámara para que entren todos los puntos no nulos. Sin puntos, deja Lima. */
    public static void encuadrar(@NonNull GoogleMap mapa, @Nullable LatLng... puntos) {
        LatLngBounds.Builder limites = new LatLngBounds.Builder();
        int cantidad = 0;
        LatLng unico = null;
        if (puntos != null) {
            for (LatLng p : puntos) {
                if (p == null) continue;
                limites.include(p);
                unico = p;
                cantidad++;
            }
        }
        if (cantidad == 0) {
            mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(LIMA, ZOOM_CIUDAD));
        } else if (cantidad == 1 || casiIguales(limites.build())) {
            // Un punto, o varios casi en el mismo sitio: encuadrarlos dejaría el mapa en el zoom máximo
            LatLng centro = cantidad == 1 ? unico : limites.build().getCenter();
            mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(centro, ZOOM_CALLE));
        } else {
            try {
                mapa.animateCamera(CameraUpdateFactory.newLatLngBounds(limites.build(), MARGEN_ENCUADRE_PX));
            } catch (IllegalStateException e) {
                // El mapa todavía no tiene tamaño (primer dibujo): se centra en el último punto
                mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(unico, ZOOM_CIUDAD));
            }
        }
    }
}
