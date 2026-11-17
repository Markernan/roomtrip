package com.example.roomtrip.cliente;

import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.example.roomtrip.R;
import com.example.roomtrip.data.model.Hotel;
import com.example.roomtrip.utils.MapaHelper;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Pestaña "Ubicación" del detalle del hotel (Lab 7): dirección, mapa con el hotel y los lugares
 * históricos cercanos que registra el administrador ({@code Hotel.lugaresCercanos}).
 *
 * <p>Las coordenadas se obtienen del texto con {@link MapaHelper#buscarLugar}: la dirección del hotel
 * ({@code Hotel.ubicacion}) y cada lugar seguido de esa dirección, para que la búsqueda quede en la
 * misma zona. El mapa usa <i>lite mode</i> (imagen estática), adecuado dentro de un ScrollView.
 */
class UbicacionHotel {

    private final AppCompatActivity activity;
    private final View contenido;
    private final List<LatLng> puntos = new ArrayList<>();
    @Nullable
    private GoogleMap mapa;
    @Nullable
    private Hotel hotel;

    UbicacionHotel(@NonNull AppCompatActivity activity, @NonNull View contenido) {
        this.activity = activity;
        this.contenido = contenido;
        SupportMapFragment fragmento = (SupportMapFragment)
                activity.getSupportFragmentManager().findFragmentById(R.id.mapaHotel);
        if (fragmento != null) {
            fragmento.getMapAsync(googleMap -> {
                mapa = googleMap;
                mapa.getUiSettings().setMapToolbarEnabled(false);
                MapaHelper.encuadrar(mapa);
                if (hotel != null) buscarPuntos(hotel);
            });
        }
    }

    void mostrar(@NonNull Hotel nuevo) {
        hotel = nuevo;
        String direccion = nuevo.getUbicacion();
        boolean hayDireccion = direccion != null && !direccion.trim().isEmpty();
        ((TextView) contenido.findViewById(R.id.tvDireccionDetalle))
                .setText(hayDireccion ? direccion : activity.getString(R.string.detalle_sin_direccion));
        mostrarLugares(nuevo.getLugaresCercanos());
        if (mapa != null) buscarPuntos(nuevo);
    }

    private void mostrarLugares(@Nullable List<String> lugares) {
        LinearLayout contenedor = contenido.findViewById(R.id.contenedorLugares);
        contenedor.removeAllViews();
        if (lugares == null || lugares.isEmpty()) {
            contenedor.addView(texto(activity.getString(R.string.detalle_sin_lugares)));
            return;
        }
        for (String lugar : lugares) {
            TextView fila = texto("•  " + lugar);
            fila.setTextColor(activity.getColor(R.color.texto_principal));
            contenedor.addView(fila);
        }
    }

    private TextView texto(String valor) {
        TextView tv = new TextView(activity);
        tv.setText(valor);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        tv.setTextColor(activity.getColor(R.color.texto_secundario));
        tv.setTypeface(ResourcesCompat.getFont(activity, R.font.poppins_regular));
        int espacio = (int) (6 * activity.getResources().getDisplayMetrics().density);
        tv.setPadding(0, espacio, 0, espacio);
        return tv;
    }

    /** Marca el hotel y luego cada lugar cercano, reencuadrando a medida que llegan las coordenadas. */
    private void buscarPuntos(@NonNull Hotel h) {
        if (mapa == null) return;
        mapa.clear();
        puntos.clear();
        String direccion = h.getUbicacion();
        String consultaHotel = direccion != null && !direccion.trim().isEmpty() ? direccion : h.getNombre();

        MapaHelper.buscarLugar(activity, consultaHotel, puntoHotel -> {
            if (mapa == null || hotel != h) return;
            if (puntoHotel != null) {
                MapaHelper.ponerMarcador(mapa, null, puntoHotel, h.getNombre(), BitmapDescriptorFactory.HUE_AZURE);
                agregarPunto(puntoHotel);
            }
            List<String> lugares = h.getLugaresCercanos();
            if (lugares == null) return;
            String zona = zonaDe(direccion);
            for (String lugar : lugares) {
                String consulta = zona != null ? lugar + ", " + zona : lugar;
                MapaHelper.buscarLugar(activity, consulta, punto -> {
                    if (mapa == null || hotel != h || punto == null) return;
                    MapaHelper.ponerMarcador(mapa, null, punto,
                            activity.getString(R.string.mapa_lugar_cercano, lugar), BitmapDescriptorFactory.HUE_ORANGE);
                    agregarPunto(punto);
                });
            }
        });
    }

    /**
     * Distrito y ciudad de la dirección del hotel, sin la calle: de "Av. Larco 245, Miraflores, Lima" queda
     * "Miraflores, Lima". Con la calle, el geocoder devuelve la dirección del hotel para cualquier lugar.
     */
    @Nullable
    static String zonaDe(@Nullable String direccion) {
        if (direccion == null || direccion.trim().isEmpty()) return null;
        int coma = direccion.indexOf(',');
        String zona = coma >= 0 ? direccion.substring(coma + 1).trim() : direccion.trim();
        return zona.isEmpty() ? null : zona;
    }

    /**
     * La pestaña empieza oculta, y con el mapa sin tamaño no se puede encuadrar varios puntos: al mostrarla
     * se vuelve a encuadrar cuando ya tiene medidas.
     */
    void alMostrar() {
        contenido.post(() -> {
            if (mapa != null && !puntos.isEmpty()) MapaHelper.encuadrar(mapa, puntos.toArray(new LatLng[0]));
        });
    }

    private void agregarPunto(@NonNull LatLng punto) {
        puntos.add(punto);
        if (mapa != null) MapaHelper.encuadrar(mapa, puntos.toArray(new LatLng[0]));
    }
}
