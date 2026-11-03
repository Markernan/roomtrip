package com.example.roomtrip.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.roomtrip.R;
import com.example.roomtrip.data.firebase.ServicioTaxiRepository;
import com.example.roomtrip.data.model.EstadoServicioTaxi;
import com.example.roomtrip.data.model.ServicioTaxi;
import com.example.roomtrip.databinding.ActivitySeguimientoTaxiBinding;
import com.example.roomtrip.utils.Constants;
import com.example.roomtrip.utils.ErroresFirebase;
import com.example.roomtrip.utils.InsetsHelper;
import com.example.roomtrip.utils.SesionFirebase;
import com.google.firebase.firestore.ListenerRegistration;

/**
 * Seguimiento del taxi en tiempo real (RF-CL-09) con el documento de {@code serviciosTaxi}:
 * nombre del taxista, origen, destino y estado. El mapa con la ubicación llega en el Lab 7.
 */
public class SeguimientoTaxiActivity extends AppCompatActivity {

    private static final String TAG = "SeguimientoTaxiActivity";

    private ActivitySeguimientoTaxiBinding binding;
    private String servicioId;
    private ListenerRegistration registro;
    private EstadoServicioTaxi ultimoEstado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SesionFirebase.uidOVolverAlLogin(this) == null) return;
        binding = ActivitySeguimientoTaxiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnVolverMapa.setOnClickListener(v -> finish());
        InsetsHelper.empujarBajoBarraEstado(binding.btnVolverMapa);
        binding.btnMostrarQr.setEnabled(false);
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
        binding.tvOrigen.setText(servicio.getHotelNombre());
        binding.tvDestino.setText(servicio.getAeropuerto());

        EstadoServicioTaxi estado = servicio.getEstado();
        String etiqueta = estado != null ? estado.getEtiqueta() : "";
        binding.tvEstadoServicio.setText(getString(R.string.cliente_taxi_estado, etiqueta));
        binding.tvNombreTaxista.setText(servicio.getTaxistaNombre() != null
                ? servicio.getTaxistaNombre() : getString(R.string.cliente_taxi_buscando));

        // El QR sirve mientras el taxista lo tiene asignado; él lo escanea al llegar (RN-011)
        binding.btnMostrarQr.setEnabled(servicio.estaEnCurso());

        if (estado == EstadoServicioTaxi.FINALIZADO && ultimoEstado != null
                && ultimoEstado != EstadoServicioTaxi.FINALIZADO) {
            Toast.makeText(this, R.string.cliente_taxi_finalizado, Toast.LENGTH_LONG).show();
        }
        ultimoEstado = estado;
    }
}
