package com.example.roomtrip.taxista;

import android.Manifest;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.roomtrip.R;
import com.example.roomtrip.data.firebase.ServicioTaxiRepository;
import com.example.roomtrip.data.model.ServicioTaxi;
import com.example.roomtrip.utils.MapaServicioTaxi;

import java.util.Map;

/**
 * Junta lo que necesitan las pantallas del servicio en curso del taxista (Lab 7): el mapa del
 * servicio, el permiso de ubicación y el reporte periódico de la posición a Firestore.
 *
 * <p>Uso: crear en {@code onCreate} (registra el pedido de permiso), llamar a {@link #mostrar} con
 * cada cambio del servicio y a {@link #detener} en {@code onStop}.
 */
public class MapaTaxista {

    private final AppCompatActivity activity;
    private final MapaServicioTaxi mapa;
    private final RastreoUbicacionTaxista rastreo;
    private final ActivityResultLauncher<String[]> pedirPermiso;

    @Nullable
    private ServicioTaxi servicio;
    private boolean permisoPedido;

    public MapaTaxista(@NonNull AppCompatActivity activity, int idFragmentoMapa,
                       @NonNull ServicioTaxiRepository repositorio) {
        this.activity = activity;
        this.mapa = new MapaServicioTaxi(activity, idFragmentoMapa);
        this.rastreo = new RastreoUbicacionTaxista(activity, repositorio, mapa::moverTaxi);
        this.pedirPermiso = activity.registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(), this::alResponderPermiso);

        if (RastreoUbicacionTaxista.tienePermiso(activity)) {
            mapa.activarMiUbicacion();
        }
    }

    /** Dibuja el servicio y se asegura de que su ubicación se esté reportando. */
    public void mostrar(@NonNull ServicioTaxi nuevo) {
        servicio = nuevo;
        mapa.actualizar(nuevo);
        if (nuevo.getId() == null || !nuevo.estaEnCurso()) {
            rastreo.detener();
            return;
        }
        if (RastreoUbicacionTaxista.tienePermiso(activity)) {
            rastreo.iniciar(nuevo.getId());
        } else if (!permisoPedido) {
            permisoPedido = true;
            pedirPermiso.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION});
        }
    }

    /** Deja de pedir posiciones (la pantalla ya no está visible o el servicio terminó). */
    public void detener() {
        rastreo.detener();
    }

    private void alResponderPermiso(@NonNull Map<String, Boolean> resultado) {
        if (RastreoUbicacionTaxista.tienePermiso(activity)) {
            mapa.activarMiUbicacion();
            if (servicio != null && servicio.getId() != null && servicio.estaEnCurso()) {
                rastreo.iniciar(servicio.getId());
            }
        } else {
            Toast.makeText(activity, R.string.mapa_permiso_ubicacion, Toast.LENGTH_LONG).show();
        }
    }
}
