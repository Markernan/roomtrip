package com.example.roomtrip.taxista;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.roomtrip.R;
import com.example.roomtrip.databinding.ActivityHistorialServiciosBinding;
import com.example.roomtrip.data.firebase.ServicioTaxiRepository;
import com.example.roomtrip.utils.ErroresFirebase;
import com.example.roomtrip.utils.NavegacionHelper;
import com.example.roomtrip.utils.SesionFirebase;

/** Historial de servicios FINALIZADOS del taxista (se abre desde su perfil). Se lee de Firestore. */
public class HistorialServiciosActivity extends AppCompatActivity {

    private static final String TAG = "HistorialServicios";

    private ActivityHistorialServiciosBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String uid = SesionFirebase.uidOVolverAlLogin(this);
        if (uid == null) return;

        binding = ActivityHistorialServiciosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavegacionHelper.configurarBarra(this, "PERFIL");
        // "Trabajo" vuelve a la pantalla principal del taxista (no solo a Perfil)
        findViewById(R.id.navTrabajo).setOnClickListener(v -> {
            Intent intent = new Intent(this, SolicitadoActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
        findViewById(R.id.navPerfil).setOnClickListener(v -> finish());

        binding.btnVolver.setOnClickListener(v -> finish());
        binding.rvHistorial.setLayoutManager(new LinearLayoutManager(this));

        // Consulta única (get()): el historial no cambia mientras se mira
        new ServicioTaxiRepository().obtenerHistorial(uid, historial -> {
            binding.tvVacio.setVisibility(historial.isEmpty() ? View.VISIBLE : View.GONE);
            binding.rvHistorial.setAdapter(new HistorialAdapter(historial));
        }, e -> {
            Log.e(TAG, "No se pudo leer el historial", e);
            binding.tvVacio.setVisibility(View.VISIBLE);
            Toast.makeText(this, ErroresFirebase.mensaje(this, e), Toast.LENGTH_LONG).show();
        });
    }
}
