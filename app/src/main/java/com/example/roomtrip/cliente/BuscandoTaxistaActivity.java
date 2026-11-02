package com.example.roomtrip.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.roomtrip.R;
import com.example.roomtrip.data.firebase.ServicioTaxiRepository;
import com.example.roomtrip.data.model.EstadoServicioTaxi;
import com.example.roomtrip.databinding.ActivityBuscandoTaxistaBinding;
import com.example.roomtrip.utils.Constants;
import com.example.roomtrip.utils.ErroresFirebase;
import com.example.roomtrip.utils.SesionFirebase;
import com.google.firebase.firestore.ListenerRegistration;

/**
 * Espera a que un taxista acepte el pedido (RF-TX-04). Escucha el documento del servicio en
 * Firestore: cuando deja de estar SOLICITADO (un taxista lo aceptó) pasa al seguimiento.
 * El pedido sigue activo aunque el cliente vuelva al inicio; lo retoma desde Mis reservas.
 */
public class BuscandoTaxistaActivity extends AppCompatActivity {

    private static final String TAG = "BuscandoTaxistaActivity";

    private String servicioId;
    private ListenerRegistration registro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SesionFirebase.uidOVolverAlLogin(this) == null) return;
        ActivityBuscandoTaxistaBinding binding = ActivityBuscandoTaxistaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnVolverAlInicio.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
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
            if (servicio.getEstado() != EstadoServicioTaxi.SOLICITADO) {
                irASeguimiento();
            }
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

    private void irASeguimiento() {
        if (registro != null) registro.remove();
        registro = null;
        Intent intent = new Intent(this, SeguimientoTaxiActivity.class);
        intent.putExtra(Constants.EXTRA_SERVICIO_TAXI_ID, servicioId);
        startActivity(intent);
        finish();
    }
}
