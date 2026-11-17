package com.example.roomtrip.taxista;

import android.Manifest;
import android.annotation.SuppressLint;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.roomtrip.R;
import com.example.roomtrip.data.firebase.HotelRepository;
import com.example.roomtrip.data.model.ServicioTaxi;
import com.example.roomtrip.utils.MapaHelper;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Mapa de la pantalla "Buscando pasajeros" del taxista (Lab 7): su posición (punto azul) y el hotel de
 * recojo de cada pedido disponible (marcador celeste, con el nombre del pasajero).
 *
 * <p>La posición del hotel sale de su dirección ({@code Hotel.ubicacion}) o, si no tiene, de su nombre,
 * con {@link MapaHelper#buscarLugar}. Pide el permiso de ubicación aquí mismo, así ya está concedido
 * cuando el taxista acepta un pedido y empieza el rastreo.
 */
class MapaPedidos {

    private static final String TAG = "MapaPedidos";

    private final AppCompatActivity activity;
    private final HotelRepository hoteles = new HotelRepository();
    private final ActivityResultLauncher<String[]> pedirPermiso;

    @Nullable
    private GoogleMap mapa;
    private List<ServicioTaxi> pedidos = new ArrayList<>();
    /** Marcador por id de pedido. */
    private final Map<String, Marker> marcadores = new HashMap<>();
    /** Coordenadas de recojo ya resueltas, por id de pedido. */
    private final Map<String, LatLng> puntos = new HashMap<>();
    private final Set<String> buscando = new HashSet<>();
    @Nullable
    private LatLng miPosicion;

    MapaPedidos(@NonNull AppCompatActivity activity, int idFragmentoMapa) {
        this.activity = activity;
        // Debe registrarse en onCreate, antes de que la Activity llegue a STARTED
        this.pedirPermiso = activity.registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(), r -> activarMiUbicacion());

        SupportMapFragment fragmento = (SupportMapFragment)
                activity.getSupportFragmentManager().findFragmentById(idFragmentoMapa);
        if (fragmento == null) {
            Log.e(TAG, "No se encontró el fragmento del mapa en el layout");
            return;
        }
        fragmento.getMapAsync(googleMap -> {
            mapa = googleMap;
            mapa.getUiSettings().setZoomControlsEnabled(true);
            mapa.getUiSettings().setMapToolbarEnabled(false);
            MapaHelper.encuadrar(mapa);
            if (RastreoUbicacionTaxista.tienePermiso(activity)) {
                activarMiUbicacion();
            } else {
                pedirPermiso.launch(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION});
            }
            mostrar(pedidos);
        });
    }

    /** Nueva lista de pedidos disponibles (llamar desde el listener de Firestore). */
    void mostrar(@NonNull List<ServicioTaxi> nuevos) {
        pedidos = new ArrayList<>(nuevos);
        if (mapa == null) return;

        Set<String> vigentes = new HashSet<>();
        for (ServicioTaxi p : pedidos) {
            if (p.getId() != null) vigentes.add(p.getId());
        }
        // Quita los pedidos que ya no están (los tomó otro taxista o se cancelaron)
        for (String id : new ArrayList<>(marcadores.keySet())) {
            if (!vigentes.contains(id)) {
                Marker m = marcadores.remove(id);
                if (m != null) m.remove();
            }
        }
        for (ServicioTaxi p : pedidos) {
            if (p.getId() == null) continue;
            if (puntos.containsKey(p.getId())) {
                ponerMarcador(p);
            } else if (buscando.add(p.getId())) {
                buscarRecojo(p);
            }
        }
        encuadrar();
    }

    private void buscarRecojo(@NonNull ServicioTaxi p) {
        if (p.getHotelId() == null) {
            buscarPorTexto(p, p.getHotelNombre());
            return;
        }
        hoteles.obtener(p.getHotelId(), hotel -> {
            String direccion = hotel != null ? hotel.getUbicacion() : null;
            boolean hayDireccion = direccion != null && !direccion.trim().isEmpty();
            buscarPorTexto(p, hayDireccion ? direccion : p.getHotelNombre());
        }, e -> {
            Log.w(TAG, "No se pudo leer el hotel " + p.getHotelId(), e);
            buscarPorTexto(p, p.getHotelNombre());
        });
    }

    private void buscarPorTexto(@NonNull ServicioTaxi p, @Nullable String texto) {
        MapaHelper.buscarLugar(activity, texto, punto -> {
            buscando.remove(p.getId());
            if (punto == null) return;
            puntos.put(p.getId(), punto);
            if (sigueDisponible(p.getId())) {
                ponerMarcador(p);
                encuadrar();
            }
        });
    }

    private boolean sigueDisponible(@NonNull String id) {
        for (ServicioTaxi p : pedidos) {
            if (id.equals(p.getId())) return true;
        }
        return false;
    }

    private void ponerMarcador(@NonNull ServicioTaxi p) {
        if (mapa == null) return;
        String hotel = p.getHotelNombre() != null ? p.getHotelNombre() : "";
        Marker m = MapaHelper.ponerMarcador(mapa, marcadores.get(p.getId()), puntos.get(p.getId()),
                activity.getString(R.string.mapa_recojo, hotel), BitmapDescriptorFactory.HUE_AZURE);
        if (m == null) return;
        if (p.getClienteNombre() != null) {
            m.setSnippet(activity.getString(R.string.mapa_pasajero, p.getClienteNombre()));
        }
        marcadores.put(p.getId(), m);
    }

    @SuppressLint("MissingPermission") // se comprueba con tienePermiso()
    private void activarMiUbicacion() {
        if (mapa == null || !RastreoUbicacionTaxista.tienePermiso(activity)) return;
        try {
            mapa.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            Log.w(TAG, "Sin permiso para mostrar la ubicación propia", e);
            return;
        }
        LocationServices.getFusedLocationProviderClient(activity).getLastLocation()
                .addOnSuccessListener(ubicacion -> {
                    if (ubicacion == null) return;
                    miPosicion = new LatLng(ubicacion.getLatitude(), ubicacion.getLongitude());
                    encuadrar();
                });
    }

    /** Encuadra al taxista y los pedidos visibles; sin ninguno, deja Lima. */
    private void encuadrar() {
        if (mapa == null) return;
        List<LatLng> visibles = new ArrayList<>();
        if (miPosicion != null) visibles.add(miPosicion);
        for (Marker m : marcadores.values()) visibles.add(m.getPosition());
        MapaHelper.encuadrar(mapa, visibles.toArray(new LatLng[0]));
    }
}
