package com.example.roomtrip.utils;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.roomtrip.R;
import com.example.roomtrip.data.firebase.HotelRepository;
import com.example.roomtrip.data.model.EstadoServicioTaxi;
import com.example.roomtrip.data.model.ServicioTaxi;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.firestore.GeoPoint;

/**
 * Mapa de un servicio de taxi (Lab 7), compartido por el cliente ({@code SeguimientoTaxiActivity})
 * y el taxista ({@code EnCaminoActivity}, {@code EnTrasladoActivity}).
 *
 * <p>Dibuja tres marcadores: el hotel (recojo), el aeropuerto (destino) y el taxi. La posición del
 * taxi sale del campo {@code ubicacionTaxista} del servicio en Firestore (cliente) o del GPS del
 * propio teléfono ({@link #moverTaxi}, taxista). La cámara encuadra el taxi y el punto al que se
 * dirige: el hotel mientras va a recoger, el aeropuerto durante el traslado.
 *
 * <p>Las coordenadas del hotel salen de su campo {@code ubicacion} (dirección escrita por el admin)
 * y las del aeropuerto del texto que eligió el cliente, ambas con {@link MapaHelper#buscarLugar}.
 */
public class MapaServicioTaxi {

    private static final String TAG = "MapaServicioTaxi";

    private final AppCompatActivity activity;
    private final HotelRepository hoteles = new HotelRepository();

    @Nullable
    private GoogleMap mapa;
    @Nullable
    private ServicioTaxi servicio;

    @Nullable
    private LatLng puntoHotel, puntoAeropuerto, puntoTaxi;
    @Nullable
    private Marker marcadorHotel, marcadorAeropuerto, marcadorTaxi;

    /** Servicio cuyos lugares ya se buscaron (para no repetir la búsqueda en cada cambio). */
    @Nullable
    private String lugaresBuscadosDe;
    @Nullable
    private EstadoServicioTaxi estadoEncuadrado;
    private boolean taxiEncuadrado;
    private boolean miUbicacionPendiente;

    public MapaServicioTaxi(@NonNull AppCompatActivity activity, int idFragmentoMapa) {
        this.activity = activity;
        SupportMapFragment fragmento = (SupportMapFragment)
                activity.getSupportFragmentManager().findFragmentById(idFragmentoMapa);
        if (fragmento == null) {
            Log.e(TAG, "No se encontró el fragmento del mapa en el layout");
            return;
        }
        fragmento.getMapAsync(this::alListo);
    }

    private void alListo(@NonNull GoogleMap googleMap) {
        mapa = googleMap;
        mapa.getUiSettings().setZoomControlsEnabled(true);
        mapa.getUiSettings().setMapToolbarEnabled(false);
        if (miUbicacionPendiente) activarMiUbicacion();
        MapaHelper.encuadrar(mapa);
        redibujar(true);
    }

    /** Punto azul de "mi ubicación" (taxista). Llamar solo con el permiso de ubicación concedido. */
    @SuppressLint("MissingPermission")
    public void activarMiUbicacion() {
        if (mapa == null) {
            miUbicacionPendiente = true;
            return;
        }
        miUbicacionPendiente = false;
        try {
            mapa.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            Log.w(TAG, "Sin permiso para mostrar la ubicación propia", e);
        }
    }

    /** Nuevo estado del servicio (llamar desde el listener de Firestore). */
    public void actualizar(@NonNull ServicioTaxi nuevo) {
        servicio = nuevo;
        GeoPoint ubicacion = nuevo.getUbicacionTaxista();
        if (ubicacion != null) puntoTaxi = new LatLng(ubicacion.getLatitude(), ubicacion.getLongitude());
        buscarLugaresSiFalta(nuevo);
        redibujar(false);
    }

    /** Posición del taxi leída del GPS del propio teléfono (pantallas del taxista). */
    public void moverTaxi(@NonNull LatLng posicion) {
        puntoTaxi = posicion;
        redibujar(false);
    }

    private void buscarLugaresSiFalta(@NonNull ServicioTaxi s) {
        if (s.getId() == null || s.getId().equals(lugaresBuscadosDe)) return;
        lugaresBuscadosDe = s.getId();

        MapaHelper.buscarLugar(activity, s.getAeropuerto(), punto -> {
            puntoAeropuerto = punto;
            redibujar(true);
        });

        if (s.getHotelId() == null) {
            buscarHotelPorNombre(s);
            return;
        }
        hoteles.obtener(s.getHotelId(), hotel -> {
            String direccion = hotel != null ? hotel.getUbicacion() : null;
            if (direccion == null || direccion.trim().isEmpty()) {
                buscarHotelPorNombre(s);
                return;
            }
            MapaHelper.buscarLugar(activity, direccion, punto -> {
                if (punto == null) {
                    buscarHotelPorNombre(s);
                } else {
                    puntoHotel = punto;
                    redibujar(true);
                }
            });
        }, e -> {
            Log.w(TAG, "No se pudo leer el hotel " + s.getHotelId(), e);
            buscarHotelPorNombre(s);
        });
    }

    private void buscarHotelPorNombre(@NonNull ServicioTaxi s) {
        MapaHelper.buscarLugar(activity, s.getHotelNombre(), punto -> {
            puntoHotel = punto;
            redibujar(true);
        });
    }

    /**
     * Pone los marcadores. La cámara se reencuadra solo cuando cambia algo importante (llega un lugar,
     * aparece el taxi por primera vez o cambia el estado), para no pelear con el usuario si mueve el mapa.
     */
    private void redibujar(boolean cambioDeLugares) {
        if (mapa == null || servicio == null) return;

        String hotel = servicio.getHotelNombre() != null ? servicio.getHotelNombre() : "";
        String aeropuerto = servicio.getAeropuerto() != null ? servicio.getAeropuerto() : "";
        marcadorHotel = MapaHelper.ponerMarcador(mapa, marcadorHotel, puntoHotel,
                activity.getString(R.string.mapa_recojo, hotel), BitmapDescriptorFactory.HUE_AZURE);
        marcadorAeropuerto = MapaHelper.ponerMarcador(mapa, marcadorAeropuerto, puntoAeropuerto,
                activity.getString(R.string.mapa_destino, aeropuerto), BitmapDescriptorFactory.HUE_RED);

        String tituloTaxi = servicio.getTaxistaNombre() != null
                ? activity.getString(R.string.mapa_taxi, servicio.getTaxistaNombre())
                : activity.getString(R.string.mapa_taxi_sin_nombre);
        marcadorTaxi = MapaHelper.ponerMarcador(mapa, marcadorTaxi, puntoTaxi, tituloTaxi,
                BitmapDescriptorFactory.HUE_YELLOW);

        boolean primerTaxi = puntoTaxi != null && !taxiEncuadrado;
        boolean cambioEstado = servicio.getEstado() != estadoEncuadrado;
        if (cambioDeLugares || primerTaxi || cambioEstado) {
            if (puntoTaxi != null) taxiEncuadrado = true;
            estadoEncuadrado = servicio.getEstado();
            MapaHelper.encuadrar(mapa, puntoTaxi, objetivo());
        }
    }

    /** A dónde va el taxi ahora: al hotel a recoger, o al aeropuerto con el pasajero. */
    @Nullable
    private LatLng objetivo() {
        if (servicio == null) return null;
        EstadoServicioTaxi estado = servicio.getEstado();
        if (estado == EstadoServicioTaxi.EN_TRASLADO || estado == EstadoServicioTaxi.FINALIZADO) {
            return puntoAeropuerto;
        }
        return puntoHotel;
    }
}
