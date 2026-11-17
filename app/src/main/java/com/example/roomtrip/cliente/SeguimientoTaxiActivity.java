package com.example.roomtrip.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.roomtrip.R;
import com.example.roomtrip.data.api.ApiException;
import com.example.roomtrip.data.api.TaxiServicios;
import com.example.roomtrip.data.firebase.ServicioTaxiRepository;
import com.example.roomtrip.data.model.EstadoServicioTaxi;
import com.example.roomtrip.data.model.ServicioTaxi;
import com.example.roomtrip.databinding.ActivitySeguimientoTaxiBinding;
import com.example.roomtrip.utils.Constants;
import com.example.roomtrip.utils.ErroresFirebase;
import com.example.roomtrip.utils.InsetsHelper;
import com.example.roomtrip.utils.MapaServicioTaxi;
import com.example.roomtrip.utils.SesionFirebase;
import com.google.firebase.firestore.ListenerRegistration;

/**
 * Seguimiento del taxi en tiempo real (RF-CL-09) con el documento de {@code serviciosTaxi}:
 * nombre del taxista, origen, destino y estado. Lab 7: mapa con el hotel, el aeropuerto y el taxi,
 * que se mueve con la ubicación que el taxista reporta en el campo {@code ubicacionTaxista}.
 */
public class SeguimientoTaxiActivity extends AppCompatActivity {

    private static final String TAG = "SeguimientoTaxiActivity";

    private ActivitySeguimientoTaxiBinding binding;
    private String servicioId;
    private ListenerRegistration registro;
    private EstadoServicioTaxi ultimoEstado;
    private MapaServicioTaxi mapa;
    private String taxistaApiId;      // id del taxista en la API, cuando ya hay uno asignado
    private String datosCargadosDe;   // taxistaApiId cuyos datos ya se pidieron a la API
    // Nombre, vehículo y valoración que dio la API. Se guarda porque el servicio cambia cada ~10 s
    // (ubicación del taxista) y cada cambio vuelve a pintar la tarjeta
    private String textoTaxistaApi;
    private boolean yaCalificado;
    private boolean calificando;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SesionFirebase.uidOVolverAlLogin(this) == null) return;
        binding = ActivitySeguimientoTaxiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnVolverMapa.setOnClickListener(v -> finish());
        InsetsHelper.empujarBajoBarraEstado(binding.btnVolverMapa);
        mapa = new MapaServicioTaxi(this, R.id.mapa);
        binding.btnMostrarQr.setEnabled(false);
        binding.ratingBarTaxista.setIsIndicator(true); // solo se puede calificar con el servicio FINALIZADO
        binding.ratingBarTaxista.setOnRatingBarChangeListener((barra, puntos, delUsuario) -> {
            if (delUsuario && puntos >= 1) calificar(Math.round(puntos));
        });
        binding.btnMostrarQr.setOnClickListener(v -> {
            Intent intent = new Intent(this, MostrarQrActivity.class);
            intent.putExtra(Constants.EXTRA_SERVICIO_TAXI_ID, servicioId);
            startActivity(intent);
        });

        servicioId = getIntent().getStringExtra(Constants.EXTRA_SERVICIO_TAXI_ID);
        if (servicioId == null) {
            Toast.makeText(this, R.string.cliente_taxi_no_encontrado, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (servicioId == null) return;
        registro = new ServicioTaxiRepository().escucharServicio(servicioId, servicio -> {
            if (servicio == null) {
                Toast.makeText(this, R.string.cliente_taxi_no_encontrado, Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            mostrar(servicio);
        }, e -> {
            Log.e(TAG, "No se pudo escuchar el servicio " + servicioId, e);
            Toast.makeText(this, ErroresFirebase.mensaje(this, e), Toast.LENGTH_LONG).show();
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (registro != null) registro.remove();
        registro = null;
    }

    private void mostrar(ServicioTaxi servicio) {
        mapa.actualizar(servicio);
        binding.tvOrigen.setText(servicio.getHotelNombre());
        binding.tvDestino.setText(servicio.getAeropuerto());

        EstadoServicioTaxi estado = servicio.getEstado();
        String etiqueta = estado != null ? estado.getEtiqueta() : "";
        binding.tvEstadoServicio.setText(getString(R.string.cliente_taxi_estado, etiqueta));
        if (servicio.getTaxistaApiId() == null || !servicio.getTaxistaApiId().equals(datosCargadosDe)) {
            textoTaxistaApi = null; // cambió el taxista (o aún no hay): no sirven los datos anteriores
        }
        if (textoTaxistaApi != null) {
            binding.tvNombreTaxista.setText(textoTaxistaApi);
        } else {
            binding.tvNombreTaxista.setText(servicio.getTaxistaNombre() != null
                    ? servicio.getTaxistaNombre() : getString(R.string.cliente_taxi_buscando));
        }

        taxistaApiId = servicio.getTaxistaApiId();
        cargarDatosDelTaxista(servicio);
        // Calificar solo es posible con el servicio terminado y un taxista conocido por la API
        binding.ratingBarTaxista.setIsIndicator(estado != EstadoServicioTaxi.FINALIZADO
                || taxistaApiId == null || yaCalificado || calificando);

        // El QR sirve mientras el taxista lo tiene asignado; él lo escanea al llegar (RN-011)
        binding.btnMostrarQr.setEnabled(servicio.estaEnCurso());

        if (estado == EstadoServicioTaxi.FINALIZADO && ultimoEstado != null
                && ultimoEstado != EstadoServicioTaxi.FINALIZADO) {
            Toast.makeText(this, R.string.cliente_taxi_finalizado, Toast.LENGTH_LONG).show();
        }
        ultimoEstado = estado;
    }

    /** Vehículo, placa y valoración del taxista asignado, tomados de la API. */
    private void cargarDatosDelTaxista(ServicioTaxi servicio) {
        String id = servicio.getTaxistaApiId();
        if (id == null || id.equals(datosCargadosDe)) return;
        datosCargadosDe = id;
        TaxiServicios.repositorio().obtenerTaxista(id, taxista -> {
            String nombre = servicio.getTaxistaNombre() != null ? servicio.getTaxistaNombre()
                    : taxista.nombreCompleto();
            String detalle = taxista.descripcionVehiculo();
            if (taxista.getPlaca() != null && !taxista.getPlaca().isEmpty()) {
                detalle = detalle.isEmpty() ? taxista.getPlaca() : detalle + " · " + taxista.getPlaca();
            }
            String texto = detalle.isEmpty() ? nombre : nombre + "\n" + detalle;
            if (!id.equals(datosCargadosDe)) return; // llegó tarde: ya hay otro taxista
            textoTaxistaApi = texto + "\n" + taxista.descripcionValoracion();
            binding.tvNombreTaxista.setText(textoTaxistaApi);
            binding.tvAvisoApi.setVisibility(View.GONE);
        }, e -> {
            // La API puede estar dormida: se queda con el nombre de Firestore, se avisa y se reintenta al volver
            Log.w(TAG, "No se pudieron cargar los datos del taxista", e);
            datosCargadosDe = null;
            ApiException error = e instanceof ApiException ? (ApiException) e : ApiException.desdeFallo(e);
            binding.tvAvisoApi.setText(error.esNoDisponible() ? ApiException.MENSAJE_NO_DISPONIBLE
                    : getString(R.string.cliente_taxi_datos_error));
            binding.tvAvisoApi.setVisibility(View.VISIBLE);
        });
    }

    /** RF-TAX-017: el cliente califica al taxista al terminar el servicio (una sola vez por servicio). */
    private void calificar(int puntuacion) {
        if (taxistaApiId == null || servicioId == null || calificando || yaCalificado) return;
        calificando = true;
        binding.ratingBarTaxista.setIsIndicator(true);
        TaxiServicios.repositorio().calificar(taxistaApiId, servicioId, puntuacion, null, calificacion -> {
            calificando = false;
            yaCalificado = true;
            Toast.makeText(this, R.string.cliente_taxi_calificacion_enviada, Toast.LENGTH_LONG).show();
        }, e -> {
            calificando = false;
            ApiException error = e instanceof ApiException ? (ApiException) e : ApiException.desdeFallo(e);
            if (error.getTipo() == ApiException.Tipo.REGLA_NEGOCIO) {
                yaCalificado = true; // este servicio ya tenía calificación
                Toast.makeText(this, R.string.cliente_taxi_ya_calificado, Toast.LENGTH_LONG).show();
            } else {
                binding.ratingBarTaxista.setRating(0);
                binding.ratingBarTaxista.setIsIndicator(false);
                Toast.makeText(this, error.esNoDisponible() ? ApiException.MENSAJE_NO_DISPONIBLE
                        : getString(R.string.cliente_taxi_calificacion_error), Toast.LENGTH_LONG).show();
            }
        });
    }
}
