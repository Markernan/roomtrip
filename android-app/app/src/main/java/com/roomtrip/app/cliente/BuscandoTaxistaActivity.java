package com.roomtrip.app.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.roomtrip.app.R;

/**
 * Espera a que un taxista acepte el pedido (RF-TX-04: al aceptar pasa a ASIGNADO).
 * TODO: reemplazar el Handler simulado por un listener de Firestore sobre el
 * documento del servicio de taxi (Lab 6/7).
 */
public class BuscandoTaxistaActivity extends AppCompatActivity {

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable simularAsignacion = this::irASeguimiento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscando_taxista);

        findViewById(R.id.btnVolverAlInicio).setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        handler.postDelayed(simularAsignacion, 2500);
    }

    private void irASeguimiento() {
        startActivity(new Intent(this, SeguimientoTaxiActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(simularAsignacion);
        super.onDestroy();
    }
}
